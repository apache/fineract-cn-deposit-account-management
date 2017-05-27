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

import io.mifos.core.command.annotation.Aggregate;
import io.mifos.core.command.annotation.CommandHandler;
import io.mifos.core.command.annotation.EventEmitter;
import io.mifos.deposit.api.v1.EventConstants;
import io.mifos.deposit.api.v1.definition.domain.ProductDefinition;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.command.CreateProductDefinitionCommand;
import io.mifos.deposit.service.internal.mapper.ProductDefinitionMapper;
import io.mifos.deposit.service.internal.repository.ActionRepository;
import io.mifos.deposit.service.internal.repository.ProductDefinitionEntity;
import io.mifos.deposit.service.internal.repository.ProductDefinitionRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Aggregate
public class ProductDefinitionAggregate {

  private final Logger logger;
  private final ProductDefinitionRepository productDefinitionRepository;
  private final ActionRepository actionRepository;

  @Autowired
  public ProductDefinitionAggregate(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                    final ProductDefinitionRepository productDefinitionRepository,
                                    final ActionRepository actionRepository) {
    super();
    this.logger = logger;
    this.productDefinitionRepository = productDefinitionRepository;
    this.actionRepository = actionRepository;
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_PRODUCT_DEFINITION)
  public String createProductDefinition(final CreateProductDefinitionCommand createProductDefinitionCommand) {

    final ProductDefinition productDefinition = createProductDefinitionCommand.productDefinition();

    final ProductDefinitionEntity productDefinitionEntity =
        ProductDefinitionMapper.map(productDefinition, this.actionRepository);

    this.productDefinitionRepository.save(productDefinitionEntity);

    return createProductDefinitionCommand.productDefinition().getIdentifier();
  }
}
