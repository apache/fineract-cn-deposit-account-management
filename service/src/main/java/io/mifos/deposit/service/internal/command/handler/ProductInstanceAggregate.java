/*
 * Copyright 2017 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.deposit.service.internal.command.handler;

import io.mifos.core.api.util.UserContextHolder;
import io.mifos.core.command.annotation.Aggregate;
import io.mifos.core.command.annotation.CommandHandler;
import io.mifos.core.command.annotation.EventEmitter;
import io.mifos.deposit.api.v1.EventConstants;
import io.mifos.deposit.api.v1.instance.domain.ProductInstance;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.command.CreateProductInstanceCommand;
import io.mifos.deposit.service.internal.mapper.ProductInstanceMapper;
import io.mifos.deposit.service.internal.repository.ProductDefinitionEntity;
import io.mifos.deposit.service.internal.repository.ProductDefinitionRepository;
import io.mifos.deposit.service.internal.repository.ProductInstanceEntity;
import io.mifos.deposit.service.internal.repository.ProductInstanceRepository;
import io.mifos.deposit.service.internal.service.helper.AccountingService;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Aggregate
public class ProductInstanceAggregate {

  private final Logger logger;
  private final ProductInstanceRepository productInstanceRepository;
  private final ProductDefinitionRepository productDefinitionRepository;
  private final AccountingService accountingService;

  @Autowired
  public ProductInstanceAggregate(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                  final ProductInstanceRepository productInstanceRepository,
                                  final ProductDefinitionRepository productDefinitionRepository,
                                  final AccountingService accountingService) {
    this.logger = logger;
    this.productInstanceRepository = productInstanceRepository;
    this.productDefinitionRepository = productDefinitionRepository;
    this.accountingService = accountingService;
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_PRODUCT_INSTANCE)
  @Transactional
  public String createProductInstance(final CreateProductInstanceCommand createProductInstanceCommand) {
    final ProductInstance productInstance = createProductInstanceCommand.productInstance();

    final ProductInstanceEntity productInstanceEntity =
        ProductInstanceMapper.map(productInstance, this.productDefinitionRepository);

    if (productInstance.getAccountIdentifier() == null) {
      final Optional<ProductDefinitionEntity> optionalProductDefinition =
          productDefinitionRepository.findByIdentifier(productInstance.getProductIdentifier());

      optionalProductDefinition.ifPresent(productDefinitionEntity -> {

        final List<ProductInstanceEntity> currentProductInstances =
            this.productInstanceRepository.findByProductDefinitionAndCustomerIdentifier(productDefinitionEntity,
                productInstance.getCustomerIdentifier());

        final int accountSuffix = currentProductInstances.size() + 1;

        final StringBuilder stringBuilder = new StringBuilder();
        final String accountNumber = stringBuilder
            .append(productDefinitionEntity.getEquityLedgerIdentifier())
            .append(".")
            .append(productInstance.getCustomerIdentifier())
            .append(".")
            .append(String.format("%05d", accountSuffix))
            .toString();

        productInstanceEntity.setAccountIdentifier(accountNumber);

        this.accountingService.createAccount(productDefinitionEntity.getEquityLedgerIdentifier(), accountNumber,
            productDefinitionEntity.getName());
      });
    }

    productInstanceEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    productInstanceEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
    productInstanceEntity.setState("PENDING");

    this.productInstanceRepository.save(productInstanceEntity);
    return productInstance.getCustomerIdentifier();
  }
}
