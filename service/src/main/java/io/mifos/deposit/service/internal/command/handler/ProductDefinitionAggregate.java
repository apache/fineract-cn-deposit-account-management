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
import io.mifos.core.lang.DateConverter;
import io.mifos.core.lang.ServiceException;
import io.mifos.deposit.api.v1.EventConstants;
import io.mifos.deposit.api.v1.definition.domain.ProductDefinition;
import io.mifos.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.command.ActivateProductDefinitionCommand;
import io.mifos.deposit.service.internal.command.CreateProductDefinitionCommand;
import io.mifos.deposit.service.internal.command.DeactivateProductDefinitionCommand;
import io.mifos.deposit.service.internal.command.DeleteProductDefinitionCommand;
import io.mifos.deposit.service.internal.command.UpdateProductDefinitionCommand;
import io.mifos.deposit.service.internal.mapper.ChargeMapper;
import io.mifos.deposit.service.internal.mapper.CurrencyMapper;
import io.mifos.deposit.service.internal.mapper.ProductDefinitionCommandMapper;
import io.mifos.deposit.service.internal.mapper.ProductDefinitionMapper;
import io.mifos.deposit.service.internal.mapper.TermMapper;
import io.mifos.deposit.service.internal.repository.ActionRepository;
import io.mifos.deposit.service.internal.repository.ChargeRepository;
import io.mifos.deposit.service.internal.repository.CurrencyRepository;
import io.mifos.deposit.service.internal.repository.ProductDefinitionCommandEntity;
import io.mifos.deposit.service.internal.repository.ProductDefinitionCommandRepository;
import io.mifos.deposit.service.internal.repository.ProductDefinitionEntity;
import io.mifos.deposit.service.internal.repository.ProductDefinitionRepository;
import io.mifos.deposit.service.internal.repository.TermRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

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

    final ProductDefinitionEntity productDefinitionEntity =
        ProductDefinitionMapper.map(productDefinition, this.actionRepository);
    productDefinitionEntity.setActive(Boolean.FALSE);

    productDefinitionEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    productDefinitionEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));

    this.productDefinitionRepository.save(productDefinitionEntity);

    return createProductDefinitionCommand.productDefinition().getIdentifier();
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_PRODUCT_DEFINITION_COMMAND)
  @Transactional
  public String activateProductdefintion(final ActivateProductDefinitionCommand activateProductDefinitionCommand) {
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
  public String deactivateProductdefintion(final DeactivateProductDefinitionCommand activateProductDefinitionCommand) {
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

      this.currencyRepository.delete(productDefinitionEntity.getCurrency());
      this.termRepository.delete(productDefinitionEntity.getTerm());
      this.chargeRepository.delete(productDefinitionEntity.getCharges());

      productDefinitionEntity.setName(productDefinition.getName());
      productDefinitionEntity.setDescription(productDefinition.getDescription());
      productDefinitionEntity.setInterest(productDefinition.getInterest());
      productDefinitionEntity.setTerm(TermMapper.map(productDefinition.getTerm()));
      productDefinitionEntity.setCharges(
          productDefinition.getCharges()
              .stream()
              .map(charge -> ChargeMapper.map(charge, this.actionRepository))
              .collect(Collectors.toList())
      );
      productDefinitionEntity.setCurrency(CurrencyMapper.map(productDefinition.getCurrency()));
      productDefinitionEntity.setMinimumBalance(productDefinition.getMinimumBalance());
      productDefinitionEntity.setFlexible(productDefinition.getFlexible());
      productDefinitionEntity.setEquityLedgerIdentifier(productDefinition.getEquityLedgerIdentifier());
      productDefinitionEntity.setExpenseAccountIdentifier(productDefinition.getExpenseAccountIdentifier());
      this.productDefinitionRepository.save(productDefinitionEntity);

      return productDefinition.getIdentifier();
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

      this.currencyRepository.delete(productDefinitionEntity.getCurrency());
      this.termRepository.delete(productDefinitionEntity.getTerm());
      this.chargeRepository.delete(productDefinitionEntity.getCharges());

      this.productDefinitionRepository.delete(productDefinitionEntity);
      return identifier;
    } else {
      this.logger.info("Could not delete product definition {0}, not found.", identifier);
      return null;
    }
  }
}
