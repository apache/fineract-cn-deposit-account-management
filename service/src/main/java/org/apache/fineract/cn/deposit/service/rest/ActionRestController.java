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
package org.apache.fineract.cn.deposit.service.rest;

import org.apache.fineract.cn.deposit.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Action;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.CreateActionCommand;
import org.apache.fineract.cn.deposit.service.internal.service.ActionService;
import java.util.List;
import javax.validation.Valid;
import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actions")
public class ActionRestController {

  private final Logger logger;
  private final CommandGateway commandGateway;
  private final ActionService actionService;

  @Autowired
  public ActionRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                              final CommandGateway commandGateway,
                              final ActionService actionService) {
    super();
    this.logger = logger;
    this.commandGateway = commandGateway;
    this.actionService = actionService;
  }

  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  public ResponseEntity<Void> create(@RequestBody @Valid final Action action) {
    this.actionService.findByIdentifier(action.getIdentifier()).ifPresent(actionEntity -> {
      throw ServiceException.conflict("Action {0} already exists.", action.getIdentifier());
    });

    this.commandGateway.process(new CreateActionCommand(action));
    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  public ResponseEntity<List<Action>> fetchActions() {
    return ResponseEntity.ok(this.actionService.fetchActions());
  }


}
