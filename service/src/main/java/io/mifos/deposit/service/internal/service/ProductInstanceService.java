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
package io.mifos.deposit.service.internal.service;

import io.mifos.accounting.api.v1.domain.Account;
import io.mifos.deposit.api.v1.instance.domain.ProductInstance;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.mapper.ProductInstanceMapper;
import io.mifos.deposit.service.internal.repository.ProductDefinitionEntity;
import io.mifos.deposit.service.internal.repository.ProductDefinitionRepository;
import io.mifos.deposit.service.internal.repository.ProductInstanceRepository;
import io.mifos.deposit.service.internal.service.helper.AccountingService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductInstanceService {

  private final Logger logger;
  private final ProductInstanceRepository productInstanceRepository;
  private final ProductDefinitionRepository productDefinitionRepository;
  private final AccountingService accountingService;

  @Autowired
  public ProductInstanceService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                final ProductInstanceRepository productInstanceRepository,
                                final ProductDefinitionRepository productDefinitionRepository,
                                final AccountingService accountingService) {
    super();
    this.logger = logger;
    this.productInstanceRepository = productInstanceRepository;
    this.productDefinitionRepository = productDefinitionRepository;
    this.accountingService = accountingService;
  }

  public List<ProductInstance> findByCustomer(final String customerIdentifier) {
    return this.productInstanceRepository.findByCustomerIdentifier(customerIdentifier)
        .stream()
        .map(productInstanceEntity -> {
          final Account account = this.accountingService.findAccount(productInstanceEntity.getAccountIdentifier());
          return ProductInstanceMapper.map(productInstanceEntity, account);
        }).
        collect(Collectors.toList());
  }

  public List<ProductInstance> findByProductDefinition(final String identifier) {
    final Optional<ProductDefinitionEntity> optionalProductDefinition = this.productDefinitionRepository.findByIdentifier(identifier);

    return optionalProductDefinition
        .map(productDefinitionEntity -> this.productInstanceRepository.findByProductDefinition(productDefinitionEntity)
          .stream()
          .map(productInstanceEntity -> {
            final Account account = this.accountingService.findAccount(productInstanceEntity.getAccountIdentifier());
            return ProductInstanceMapper.map(productInstanceEntity, account);
          })
          .collect(Collectors.toList())).orElseGet(Collections::emptyList);

  }

  public Optional<ProductInstance> findByAccountIdentifier(final String identifier) {
    return this.productInstanceRepository.findByAccountIdentifier(identifier).map(productInstanceEntity -> {
      final Account account = this.accountingService.findAccount(productInstanceEntity.getAccountIdentifier());
      return ProductInstanceMapper.map(productInstanceEntity, account);
    });
  }
}
