/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.deposit.service.internal.command.handler;

import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.ActivateProductInstanceCommand;
import org.apache.fineract.cn.deposit.service.internal.command.CloseProductInstanceCommand;
import org.apache.fineract.cn.deposit.service.internal.command.CreateProductInstanceCommand;
import org.apache.fineract.cn.deposit.service.internal.command.TransactionProcessedCommand;
import org.apache.fineract.cn.deposit.service.internal.command.UpdateProductInstanceCommand;
import org.apache.fineract.cn.deposit.service.internal.mapper.ProductInstanceMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductInstanceEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductInstanceRepository;
import org.apache.fineract.cn.deposit.service.internal.service.helper.AccountingService;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.cn.accounting.api.v1.domain.Account;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

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

    final Optional<ProductDefinitionEntity> optionalProductDefinition =
        productDefinitionRepository.findByIdentifier(productInstance.getProductIdentifier());

    optionalProductDefinition.ifPresent(productDefinitionEntity -> {

      final List<ProductInstanceEntity> currentProductInstances =
          this.productInstanceRepository.findByCustomerIdentifier(productInstance.getCustomerIdentifier());

      final int accountSuffix = currentProductInstances.size() + 1;
      final String accountNumber =
          productInstance.getCustomerIdentifier() +
              "." + productDefinitionEntity.getEquityLedgerIdentifier() +
              "." + String.format("%05d", accountSuffix);

      productInstanceEntity.setAccountIdentifier(accountNumber);

      this.accountingService.createAccount(productDefinitionEntity.getEquityLedgerIdentifier(),
          productDefinitionEntity.getName(), productInstanceEntity.getCustomerIdentifier(),
          accountNumber, productInstance.getAlternativeAccountNumber(),
          productInstance.getBalance());
    });

    productInstanceEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    productInstanceEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
    productInstanceEntity.setState("PENDING");

    this.productInstanceRepository.save(productInstanceEntity);
    return productInstance.getCustomerIdentifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.ACTIVATE_PRODUCT_INSTANCE)
  public String process(final ActivateProductInstanceCommand activateProductInstanceCommand) {
    final String accountIdentifier = activateProductInstanceCommand.identifier();
    final Optional<ProductInstanceEntity> optionalProductInstance =
        this.productInstanceRepository.findByAccountIdentifier(accountIdentifier);

    if (optionalProductInstance.isPresent()) {
      final ProductInstanceEntity productInstanceEntity = optionalProductInstance.get();
      productInstanceEntity.setState("ACTIVE");
      if (productInstanceEntity.getOpenedOn() == null) {
        productInstanceEntity.setOpenedOn(LocalDate.now(Clock.systemUTC()));
      }
      productInstanceEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
      productInstanceEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
      this.productInstanceRepository.save(productInstanceEntity);

      return accountIdentifier;
    } else {
      this.logger.warn("Product instance for account {} not found.", accountIdentifier);
    }

    return null;
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.CLOSE_PRODUCT_INSTANCE)
  public String process(final CloseProductInstanceCommand closeProductInstanceCommand) {
    final String accountIdentifier = closeProductInstanceCommand.identifier();
    final Optional<ProductInstanceEntity> optionalProductInstance =
        this.productInstanceRepository.findByAccountIdentifier(accountIdentifier);

    if (optionalProductInstance.isPresent()) {
      final ProductInstanceEntity productInstanceEntity = optionalProductInstance.get();
      productInstanceEntity.setState("CLOSED");
      productInstanceEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
      productInstanceEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
      this.productInstanceRepository.save(productInstanceEntity);

      return accountIdentifier;
    } else {
      this.logger.warn("Product instance for account {} not found.", accountIdentifier);
    }

    return null;
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.PUT_PRODUCT_INSTANCE)
  public String process(final UpdateProductInstanceCommand updateProductInstanceCommand) {
    final ProductInstance productInstance = updateProductInstanceCommand.productInstance();
    final Optional<ProductInstanceEntity> optionalProductInstance =
        this.productInstanceRepository.findByAccountIdentifier(productInstance.getAccountIdentifier());

    if (optionalProductInstance.isPresent()) {
      final ProductInstanceEntity productInstanceEntity = optionalProductInstance.get();

      if (this.hasChanged(productInstance, productInstanceEntity)) {
        final Account account = this.accountingService.findAccount(productInstanceEntity.getAccountIdentifier());
        account.setSignatureAuthorities(productInstance.getBeneficiaries());
        this.accountingService.updateAccount(account);

        productInstanceEntity.setBeneficiaries(
            productInstance.getBeneficiaries().stream().collect(Collectors.joining(",")));
        productInstanceEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        productInstanceEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
        this.productInstanceRepository.save(productInstanceEntity);
        return productInstance.getAccountIdentifier();
      } else {
        this.logger.info("Skipped update for product instance {}, no data changed.", productInstance.getAccountIdentifier());
        return null;
      }
    } else {
      throw ServiceException.notFound("Product instance {0} not found.", productInstance.getAccountIdentifier());
    }
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.PUT_PRODUCT_INSTANCE)
  public String process(final TransactionProcessedCommand transactionProcessedCommand) {
    final Optional<ProductInstanceEntity> optionalProductInstance =
        this.productInstanceRepository.findByAccountIdentifier(transactionProcessedCommand.accountIdentifier());

    final ProductInstanceEntity productInstanceEntity = optionalProductInstance.orElseThrow(() ->
        ServiceException.notFound("Product instance {0} not found.", transactionProcessedCommand.accountIdentifier()));

    productInstanceEntity.setLastTransactionDate(LocalDateTime.now(Clock.systemUTC()));
    
    this.productInstanceRepository.save(productInstanceEntity);

    return transactionProcessedCommand.accountIdentifier();
  }

  private boolean hasChanged(final ProductInstance productInstance, final ProductInstanceEntity productInstanceEntity) {
    if (productInstance.getBeneficiaries() != null) {
      if (productInstanceEntity.getBeneficiaries() == null) {
        return true;
      }

      final HashSet<String> knownBeneficiaries = new HashSet<>(
          Arrays.asList(StringUtils.split(productInstanceEntity.getBeneficiaries(), ","))
      );

      if (knownBeneficiaries.size() != productInstance.getBeneficiaries().size()
          || !knownBeneficiaries.containsAll(productInstance.getBeneficiaries())) {
        return true;
      }
    }

    return productInstanceEntity.getBeneficiaries() == null;
  }
}
