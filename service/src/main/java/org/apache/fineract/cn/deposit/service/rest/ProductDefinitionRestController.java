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
import org.apache.fineract.cn.deposit.api.v1.definition.domain.DividendDistribution;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.api.v1.domain.Type;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.ActivateProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.CreateProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.DeactivateProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.DeleteProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.DividendDistributionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.UpdateProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.service.ProductDefinitionService;
import org.apache.fineract.cn.deposit.service.internal.service.ProductInstanceService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/definitions")
public class ProductDefinitionRestController {

  private final Logger logger;
  private final CommandGateway commandGateway;
  private final ProductDefinitionService productDefinitionService;
  private final ProductInstanceService productInstanceService;

  @Autowired
  public ProductDefinitionRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                         final CommandGateway commandGateway,
                                         final ProductDefinitionService productDefinitionService,
                                         final ProductInstanceService productInstanceService) {
    super();
    this.logger = logger;
    this.commandGateway = commandGateway;
    this.productDefinitionService = productDefinitionService;
    this.productInstanceService = productInstanceService;
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<Void> create(@RequestBody @Valid final ProductDefinition productDefinition) {
    if (!productDefinition.getType().equals(Type.SHARE.name())
        && productDefinition.getAccrueAccountIdentifier() == null) {
      throw ServiceException.badRequest("Accrue account must be given.");
    }

    if (this.productDefinitionService.findProductDefinition(productDefinition.getIdentifier()).isPresent()) {
      throw ServiceException.conflict("Product definition{0} already exists.", productDefinition.getIdentifier());
    } else {
      this.commandGateway.process(new CreateProductDefinitionCommand(productDefinition));
      return ResponseEntity.accepted().build();
    }
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<List<ProductDefinition>> fetchProductDefinitions() {
    return ResponseEntity.ok(this.productDefinitionService.fetchProductDefinitions());
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<ProductDefinition> findProductDefinition(@PathVariable("identifier") final String identifier) {
    return ResponseEntity.ok(
        this.productDefinitionService.findProductDefinition(identifier)
            .orElseThrow(() -> ServiceException.notFound("Product definition {0} not found.", identifier))
    );
  }

  @RequestMapping(
      value = "/{identifier}/instances",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  public ResponseEntity<List<ProductInstance>> findProductInstances(@PathVariable("identifier") final String identifier) {

    final Optional<ProductDefinition> optionalProductDefinition = this.productDefinitionService.findProductDefinition(identifier);
    if (!this.productDefinitionService.findProductDefinition(identifier).isPresent()) {
      throw ServiceException.notFound("Product definition {0} not found.", identifier);
    } else {
      return ResponseEntity.ok(this.productInstanceService.findByProductDefinition(identifier));
    }
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

    final Optional<ProductDefinition> optionalProductDefinition = this.productDefinitionService.findProductDefinition(identifier);
    if (!optionalProductDefinition.isPresent()) {
      throw ServiceException.notFound("Product definition {0} not found.", identifier);
    } else {
      switch (ProductDefinitionCommand.Action.valueOf(command.getAction())) {
        case ACTIVATE:
          this.commandGateway.process(new ActivateProductDefinitionCommand(identifier, command));
          break;
        case DEACTIVATE:
          this.commandGateway.process(new DeactivateProductDefinitionCommand(identifier, command));
          break;
        default:
          throw ServiceException.badRequest("Unsupported product definition command {0}.", command.getAction());
      }
      return ResponseEntity.accepted().build();
    }
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}/commands",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<List<ProductDefinitionCommand>> getProductDefinitionCommands(@PathVariable("identifier") final String identifier) {

    final Optional<ProductDefinition> optionalProductDefinition = this.productDefinitionService.findProductDefinition(identifier);
    if (!optionalProductDefinition.isPresent()) {
      throw ServiceException.notFound("Product definition {0} not found.", identifier);
    } else {
      return ResponseEntity.ok(this.productDefinitionService.findCommands(identifier));
    }
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<Void> changeProductDefinition(@PathVariable("identifier") final String identifier,
                                               @RequestBody @Valid ProductDefinition productDefinition) {
    if (!identifier.equals(productDefinition.getIdentifier())) {
      throw ServiceException.badRequest("Given product definition must match path {0}.", identifier);
    }

    final Optional<ProductDefinition> optionalProductDefinition = this.productDefinitionService.findProductDefinition(identifier);
    if (!optionalProductDefinition.isPresent()) {
      throw ServiceException.notFound("Product Definition {0} not found", identifier);
    } else {
      final ProductDefinition currentProductDefinition = optionalProductDefinition.get();
      if (!currentProductDefinition.getFlexible()
          && !currentProductDefinition.getInterest().equals(productDefinition.getInterest())) {
        throw ServiceException.badRequest("Interest of product {0} rate is not flexible.", productDefinition.getIdentifier());
      }
    }

    this.commandGateway.process(new UpdateProductDefinitionCommand(productDefinition));

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}",
      method = RequestMethod.DELETE,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<Void> deleteProductDefinition(@PathVariable("identifier") final String identifier) {
    if (!this.productDefinitionService.findProductDefinition(identifier).isPresent()) {
      throw ServiceException.notFound("Product Definition {0} not found", identifier);
    }

    if (!this.productInstanceService.findByProductDefinition(identifier).isEmpty()) {
      throw ServiceException.conflict("Product Definition {0} has assigned instances.", identifier);
    }

    this.commandGateway.process(new DeleteProductDefinitionCommand(identifier));

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}/dividends",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<Void> dividendDistribution(@PathVariable("identifier") final String identifier,
                                            @RequestBody @Valid final DividendDistribution dividendDistribution) {
    final Optional<ProductDefinition> optionalProductDefinition = this.productDefinitionService.findProductDefinition(identifier);
    if (!optionalProductDefinition.isPresent()) {
      throw ServiceException.notFound("Product definition {0} not found", identifier);
    } else {
      final ProductDefinition productDefinition = optionalProductDefinition.get();
      if (!productDefinition.getType().equals(Type.SHARE.name())) {
        throw ServiceException.badRequest("Product definition {0} is not a share product.", identifier);
      }
    }

    final LocalDate dueDate = dividendDistribution.getDueDate().toLocalDate();
    final Double amount = Double.valueOf(dividendDistribution.getDividendRate());

    this.commandGateway.process(new DividendDistributionCommand(identifier, dueDate, amount));

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}/dividends",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<List<DividendDistribution>> fetchDividendDistributions(@PathVariable("identifier") final String identifier) {
    return ResponseEntity.ok(
        this.productDefinitionService.fetchDividendDistributions(identifier)
    );
  }
}
