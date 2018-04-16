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
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Action;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.CreateActionCommand;
import org.apache.fineract.cn.deposit.service.internal.mapper.ActionMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.ActionRepository;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
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
