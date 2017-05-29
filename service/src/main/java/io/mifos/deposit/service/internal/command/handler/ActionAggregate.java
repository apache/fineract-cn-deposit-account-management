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
import io.mifos.deposit.api.v1.definition.domain.Action;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.command.CreateActionCommand;
import io.mifos.deposit.service.internal.mapper.ActionMapper;
import io.mifos.deposit.service.internal.repository.ActionRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

@Aggregate
public class ActionAggregate {

  private final Logger logger;
  private final ActionRepository actionRepository;

  @Autowired
  public ActionAggregate(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                         final ActionRepository actionRepository) {
    this.logger = logger;
    this.actionRepository = actionRepository;
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_PRODUCT_ACTION)
  @Transactional
  public String createAction(final CreateActionCommand createActionCommand) {
    final Action action = createActionCommand.action();
    this.actionRepository.save(ActionMapper.map(action));
    this.logger.debug("Action {} created.", action.getIdentifier());
    return action.getIdentifier();
  }
}
