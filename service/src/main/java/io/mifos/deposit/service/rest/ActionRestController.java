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
package io.mifos.deposit.service.rest;

import io.mifos.anubis.annotation.AcceptedTokenType;
import io.mifos.anubis.annotation.Permittable;
import io.mifos.core.command.gateway.CommandGateway;
import io.mifos.core.lang.ServiceException;
import io.mifos.deposit.api.v1.PermittableGroupIds;
import io.mifos.deposit.api.v1.definition.domain.Action;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.command.CreateActionCommand;
import io.mifos.deposit.service.internal.service.ActionService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

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
