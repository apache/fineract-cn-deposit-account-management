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
import io.mifos.deposit.api.v1.PermittableGroupIds;
import io.mifos.deposit.api.v1.definition.domain.ProductDefinition;
import io.mifos.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import io.mifos.deposit.service.ServiceConstants;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/definitions")
public class ProductDefinitionRestController {

  private final Logger logger;
  private final CommandGateway commandGateway;

  @Autowired
  public ProductDefinitionRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                         final CommandGateway commandGateway) {
    super();
    this.logger = logger;
    this.commandGateway = commandGateway;
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<Void> create(@RequestBody @Valid final ProductDefinition productDefinition) {
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<List<ProductDefinition>> fetchProductDefinitions() {
    return ResponseEntity.ok(Collections.emptyList());
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<ProductDefinition> findProductDefinition(@PathVariable("identifier") final String Identifier) {
    return ResponseEntity.ok(null);
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}/commands",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<Void> process(@PathVariable("identifier") final String identifier,
                                      @RequestBody @Valid final ProductDefinitionCommand command) {
    return ResponseEntity.accepted().build();
  }
}
