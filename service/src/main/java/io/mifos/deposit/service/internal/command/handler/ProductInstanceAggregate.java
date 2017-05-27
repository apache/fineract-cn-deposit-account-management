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
import io.mifos.deposit.api.v1.instance.domain.ProductInstance;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.command.CreateProductInstanceCommand;
import io.mifos.deposit.service.internal.mapper.ProductInstanceMapper;
import io.mifos.deposit.service.internal.repository.ProductDefinitionRepository;
import io.mifos.deposit.service.internal.repository.ProductInstanceEntity;
import io.mifos.deposit.service.internal.repository.ProductInstanceRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Aggregate
public class ProductInstanceAggregate {

  private final Logger logger;
  private final ProductInstanceRepository productInstanceRepository;
  private final ProductDefinitionRepository productDefinitionRepository;

  @Autowired
  public ProductInstanceAggregate(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                  final ProductInstanceRepository productInstanceRepository,
                                  final ProductDefinitionRepository productDefinitionRepository) {
    this.logger = logger;
    this.productInstanceRepository = productInstanceRepository;
    this.productDefinitionRepository = productDefinitionRepository;
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_PRODUCT_INSTANCE)
  public String createProductInstance(final CreateProductInstanceCommand createProductInstanceCommand) {
    final ProductInstance productInstance = createProductInstanceCommand.productInstance();

    final ProductInstanceEntity productInstanceEntity =
        ProductInstanceMapper.map(productInstance, this.productDefinitionRepository);

    this.productInstanceRepository.save(productInstanceEntity);
    return productInstance.getCustomerIdentifier();
  }
}
