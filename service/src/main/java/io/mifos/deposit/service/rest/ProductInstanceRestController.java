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
import io.mifos.deposit.api.v1.EventConstants;
import io.mifos.deposit.api.v1.PermittableGroupIds;
import io.mifos.deposit.api.v1.instance.domain.ProductInstance;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.command.ActivateProductInstanceCommand;
import io.mifos.deposit.service.internal.command.CloseProductInstanceCommand;
import io.mifos.deposit.service.internal.command.CreateProductInstanceCommand;
import io.mifos.deposit.service.internal.service.ProductInstanceService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/instances")
public class ProductInstanceRestController {

  private final Logger logger;
  private final CommandGateway commandGateway;
  private final ProductInstanceService productInstanceService;

  @Autowired
  public ProductInstanceRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                       final CommandGateway commandGateway,
                                       final ProductInstanceService productInstanceService) {
    super();
    this.logger = logger;
    this.commandGateway = commandGateway;
    this.productInstanceService = productInstanceService;
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<Void> create(@RequestBody @Valid final ProductInstance productInstance) {
    this.commandGateway.process(new CreateProductInstanceCommand(productInstance));
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<List<ProductInstance>> fetchProductInstances(@RequestParam(value = "customer", required = true) final String customerIdentifier) {
    return ResponseEntity.ok(this.productInstanceService.findByCustomer(customerIdentifier));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<Void> postProductInstanceCommand(@PathVariable("identifier") final String identifier,
                                                  @RequestParam(value = "command", required = true) final String command) {
    if (!this.productInstanceService.findByAccountIdentifier(identifier).isPresent()) {
      throw ServiceException.notFound("Product instance {0} not found.", identifier);
    }

    switch (command.toUpperCase()) {
      case EventConstants.ACTIVATE_PRODUCT_INSTANCE_COMMAND:
        this.commandGateway.process(new ActivateProductInstanceCommand(identifier));
        break;
      case EventConstants.CLOSE_PRODUCT_INSTANCE_COMMAND:
        this.commandGateway.process(new CloseProductInstanceCommand(identifier));
        break;
      default:
        throw ServiceException.badRequest("Unsupported command {0}", command);
    }

    return ResponseEntity.accepted().build();
  }
}
