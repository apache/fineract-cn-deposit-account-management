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
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.ActivateProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.CreateProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.DeactivateProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.DeleteProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.UpdateProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.mapper.ChargeMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.CurrencyMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.ProductDefinitionCommandMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.ProductDefinitionMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.TermMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.ActionRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ChargeEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ChargeRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.CurrencyEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.CurrencyRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionCommandEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionCommandRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.TermEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.TermRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.DateConverter;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

@Aggregate
public class ProductDefinitionAggregate {

  private final Logger logger;
  private final ProductDefinitionRepository productDefinitionRepository;
  private final ActionRepository actionRepository;
  private final ProductDefinitionCommandRepository productDefinitionCommandRepository;
  private final ChargeRepository chargeRepository;
  private final CurrencyRepository currencyRepository;
  private final TermRepository termRepository;

  @Autowired
  public ProductDefinitionAggregate(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                    final ProductDefinitionRepository productDefinitionRepository,
                                    final ActionRepository actionRepository,
                                    final ProductDefinitionCommandRepository productDefinitionCommandRepository,
                                    final ChargeRepository chargeRepository,
                                    final CurrencyRepository currencyRepository,
                                    final TermRepository termRepository) {
    super();
    this.logger = logger;
    this.productDefinitionRepository = productDefinitionRepository;
    this.actionRepository = actionRepository;
    this.productDefinitionCommandRepository = productDefinitionCommandRepository;
    this.chargeRepository = chargeRepository;
    this.currencyRepository = currencyRepository;
    this.termRepository = termRepository;
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_PRODUCT_DEFINITION)
  @Transactional
  public String createProductDefinition(final CreateProductDefinitionCommand createProductDefinitionCommand) {

    final ProductDefinition productDefinition = createProductDefinitionCommand.productDefinition();

    final ProductDefinitionEntity productDefinitionEntity = ProductDefinitionMapper.map(productDefinition);
    productDefinitionEntity.setActive(Boolean.FALSE);

    productDefinitionEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    productDefinitionEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));

    final ProductDefinitionEntity savedProductEntity = this.productDefinitionRepository.save(productDefinitionEntity);

    this.saveDependingEntities(productDefinition, savedProductEntity);

    return createProductDefinitionCommand.productDefinition().getIdentifier();
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_PRODUCT_DEFINITION_COMMAND)
  @Transactional
  public String activateProductDefinition(final ActivateProductDefinitionCommand activateProductDefinitionCommand) {
    final Optional<ProductDefinitionEntity> optionalProductDefinition = productDefinitionRepository.findByIdentifier(activateProductDefinitionCommand.identifier());

    if (optionalProductDefinition.isPresent()) {
      final ProductDefinitionCommand command = activateProductDefinitionCommand.command();
      command.setCreatedBy(UserContextHolder.checkedGetUser());
      command.setCreatedOn(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())));

      final ProductDefinitionEntity productDefinitionEntity = optionalProductDefinition.get();
      productDefinitionEntity.setActive(Boolean.TRUE);
      productDefinitionEntity.setLastModifiedBy(command.getCreatedBy());
      productDefinitionEntity.setLastModifiedOn(DateConverter.fromIsoString(command.getCreatedOn()));
      final ProductDefinitionEntity savedProductDefinitionEntity = this.productDefinitionRepository.save(productDefinitionEntity);

      final ProductDefinitionCommandEntity productDefinitionCommandEntity = ProductDefinitionCommandMapper.map(command);
      productDefinitionCommandEntity.setProductDefinition(savedProductDefinitionEntity);

      this.productDefinitionCommandRepository.save(productDefinitionCommandEntity);
      return activateProductDefinitionCommand.identifier();
    } else {
      this.logger.warn("Could not activate production definition {}, not found.", activateProductDefinitionCommand.identifier());
      return null;
    }
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_PRODUCT_DEFINITION_COMMAND)
  @Transactional
  public String deactivateProductDefinition(final DeactivateProductDefinitionCommand activateProductDefinitionCommand) {
    final Optional<ProductDefinitionEntity> optionalProductDefinition = productDefinitionRepository.findByIdentifier(activateProductDefinitionCommand.identifier());

    if (optionalProductDefinition.isPresent()) {
      final ProductDefinitionCommand command = activateProductDefinitionCommand.command();
      command.setCreatedBy(UserContextHolder.checkedGetUser());
      command.setCreatedOn(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())));

      final ProductDefinitionEntity productDefinitionEntity = optionalProductDefinition.get();
      productDefinitionEntity.setActive(Boolean.FALSE);
      productDefinitionEntity.setLastModifiedBy(command.getCreatedBy());
      productDefinitionEntity.setLastModifiedOn(DateConverter.fromIsoString(command.getCreatedOn()));
      final ProductDefinitionEntity savedProductDefinitionEntity = this.productDefinitionRepository.save(productDefinitionEntity);

      final ProductDefinitionCommandEntity productDefinitionCommandEntity = ProductDefinitionCommandMapper.map(command);
      productDefinitionCommandEntity.setProductDefinition(savedProductDefinitionEntity);

      this.productDefinitionCommandRepository.save(productDefinitionCommandEntity);
      return activateProductDefinitionCommand.identifier();
    } else {
      this.logger.warn("Could not activate production definition {}, not found.", activateProductDefinitionCommand.identifier());
      return null;
    }
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.PUT_PRODUCT_DEFINITION)
  public String process(final UpdateProductDefinitionCommand updateProductDefinitionCommand) {
    final ProductDefinition productDefinition = updateProductDefinitionCommand.productDefinition();

    final Optional<ProductDefinitionEntity> optionalProductDefinition =
        this.productDefinitionRepository.findByIdentifier(productDefinition.getIdentifier());

    if (optionalProductDefinition.isPresent()) {
      final ProductDefinitionEntity productDefinitionEntity = optionalProductDefinition.get();

      this.deleteDependingEntities(productDefinitionEntity);

      productDefinitionEntity.setName(productDefinition.getName());
      productDefinitionEntity.setDescription(productDefinition.getDescription());
      productDefinitionEntity.setInterest(productDefinition.getInterest());
      productDefinitionEntity.setMinimumBalance(productDefinition.getMinimumBalance());
      productDefinitionEntity.setFlexible(productDefinition.getFlexible());
      productDefinitionEntity.setEquityLedgerIdentifier(productDefinition.getEquityLedgerIdentifier());
      productDefinitionEntity.setExpenseAccountIdentifier(productDefinition.getExpenseAccountIdentifier());
      final ProductDefinitionEntity savedProductDefinition = this.productDefinitionRepository.save(productDefinitionEntity);

      this.saveDependingEntities(productDefinition, savedProductDefinition);

      return productDefinitionEntity.getIdentifier();
    } else {
      throw ServiceException.notFound("Product definition {0} not found.", productDefinition.getIdentifier());
    }
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.DELETE_PRODUCT_DEFINITION)
  public String process(final DeleteProductDefinitionCommand deleteProductDefinitionCommand) {
    final String identifier = deleteProductDefinitionCommand.identifier();

    final Optional<ProductDefinitionEntity> optionalProductDefinition = this.productDefinitionRepository.findByIdentifier(identifier);

    if (optionalProductDefinition.isPresent()) {
      final ProductDefinitionEntity productDefinitionEntity = optionalProductDefinition.get();

      this.productDefinitionCommandRepository.delete(
          this.productDefinitionCommandRepository.findByProductDefinition(productDefinitionEntity)
      );
      this.productDefinitionCommandRepository.flush();

      this.deleteDependingEntities(productDefinitionEntity);

      this.productDefinitionRepository.delete(productDefinitionEntity);
      return identifier;
    } else {
      this.logger.info("Could not delete product definition {0}, not found.", identifier);
      return null;
    }
  }

  void saveDependingEntities(final ProductDefinition productDefinition, final ProductDefinitionEntity savedProductEntity) {
    final CurrencyEntity currencyEntity = CurrencyMapper.map(productDefinition.getCurrency());
    currencyEntity.setProductDefinition(savedProductEntity);
    this.currencyRepository.save(currencyEntity);

    final TermEntity termEntity = TermMapper.map(productDefinition.getTerm());
    termEntity.setProductDefinition(savedProductEntity);
    this.termRepository.save(termEntity);

    if (productDefinition.getCharges() != null && !productDefinition.getCharges().isEmpty()) {
      this.chargeRepository.save(productDefinition.getCharges()
              .stream()
              .map(charge -> {
                final ChargeEntity chargeEntity = ChargeMapper.map(charge, this.actionRepository);
                chargeEntity.setProductDefinition(savedProductEntity);
                return chargeEntity;
              })
              .collect(Collectors.toSet())
      );
    }
  }

  void deleteDependingEntities(final ProductDefinitionEntity productDefinitionEntity) {
    final CurrencyEntity currencyEntity = this.currencyRepository.findByProductDefinition(productDefinitionEntity);
    this.currencyRepository.delete(currencyEntity);
    this.currencyRepository.flush();

    final TermEntity termEntity = this.termRepository.findByProductDefinition(productDefinitionEntity);
    this.termRepository.delete(termEntity);
    this.termRepository.flush();

    final List<ChargeEntity> chargeEntities = this.chargeRepository.findByProductDefinition(productDefinitionEntity);
    this.chargeRepository.delete(chargeEntities);
    this.chargeRepository.flush();
  }
}
