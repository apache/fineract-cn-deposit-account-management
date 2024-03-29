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

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.AvailableTransactionType;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.BalanceResponse;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.StatementResponse;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.*;
import org.apache.fineract.cn.deposit.service.internal.service.ProductInstanceService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.deposit.service.internal.service.TransactionService;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/instances")
public class ProductInstanceRestController {

  private final Logger logger;
  private final CommandGateway commandGateway;
  private final ProductInstanceService productInstanceService;
  private final TransactionService transactionService;


  @Value("${config.txnMaxRetry}")
  private Integer txnMaxRetry;

  @Autowired
  public ProductInstanceRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                       final CommandGateway commandGateway,
                                       final ProductInstanceService productInstanceService,
                                       TransactionService transactionService) {
    super();
    this.logger = logger;
    this.commandGateway = commandGateway;
    this.productInstanceService = productInstanceService;
    this.transactionService = transactionService;
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<Void> create(@RequestBody @Valid final ProductInstance productInstance)  throws Throwable{
    int retryCount = 0;
    Exception e = null;
    do {
      retryCount++;
      logger.info("Try transaction :  " + retryCount + " of " + txnMaxRetry);
      System.out.println("*******Try transaction :  " + retryCount + " of " + txnMaxRetry);
      try {
        this.commandGateway.process(new CreateProductInstanceCommand(productInstance));
        return ResponseEntity.accepted().build();
      } catch (Exception ex) {
        logger.info(ex.getClass().getCanonicalName());
        System.out.println(ex.getClass().getCanonicalName());
        logger.info(ex.getClass().getName());
        System.out.println(ex.getClass().getName());
        logger.info(ex.getMessage());
        System.out.println(ex.getMessage());
        e=ex;
      }
    } while (retryCount < txnMaxRetry);
    //throw the last exception
    throw e;
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<List<ProductInstance>> fetchProductInstances(
      @RequestParam(value = "customer", required = true) final String customerIdentifier) {
    return ResponseEntity.ok(this.productInstanceService.findByCustomer(customerIdentifier));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
      value = "/transactiontypes",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ResponseEntity<Set<AvailableTransactionType>> fetchPossibleTransactionTypes(
      @RequestParam(value = "customer", required = true) final String customerIdentifier) {
    final HashSet<AvailableTransactionType> availableTransactionTypes = new HashSet<>();
    final List<ProductInstance> productInstances = this.productInstanceService.findByCustomer(customerIdentifier);
    productInstances.forEach(productInstance -> {
      if (productInstance.getState().equals("PENDING")) {
        final AvailableTransactionType actionOpen = new AvailableTransactionType();
        actionOpen.setTransactionType("ACCO");
        availableTransactionTypes.add(actionOpen);
      } else if (productInstance.getState().equals("ACTIVE")) {
        final AvailableTransactionType actionDeposit = new AvailableTransactionType();
        actionDeposit.setTransactionType("CDPT");
        availableTransactionTypes.add(actionDeposit);
        final AvailableTransactionType actionWithdrawal = new AvailableTransactionType();
        actionWithdrawal.setTransactionType("CWDL");
        availableTransactionTypes.add(actionWithdrawal);
        final AvailableTransactionType actionTransfer = new AvailableTransactionType();
        actionTransfer.setTransactionType("ACCT");
        availableTransactionTypes.add(actionTransfer);
        final AvailableTransactionType actionClose = new AvailableTransactionType();
        actionClose.setTransactionType("ACCC");
        availableTransactionTypes.add(actionClose);
        final AvailableTransactionType actionCheque = new AvailableTransactionType();
        actionCheque.setTransactionType("CCHQ");
        availableTransactionTypes.add(actionCheque);
      }
    });
    return ResponseEntity.ok(availableTransactionTypes);
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
      case EventConstants.PRODUCT_INSTANCE_TRANSACTION:
        this.commandGateway.process(new TransactionProcessedCommand(identifier));
        break;
      default:
        throw ServiceException.badRequest("Unsupported command {0}", command);
    }

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<Void> change(@PathVariable("identifier") final String identifier,
                              @RequestBody @Valid final ProductInstance productInstance) {
    if (!identifier.equals(productInstance.getAccountIdentifier())) {
      throw ServiceException.badRequest("Given product instance must match path {0}", identifier);
    }

    if (!this.productInstanceService.findByAccountIdentifier(identifier).isPresent()) {
      throw ServiceException.notFound("Product instance {0} not found.", identifier);
    }

    this.commandGateway.process(new UpdateProductInstanceCommand(productInstance));

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
      value = "/{identifier}",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<ProductInstance> find(@PathVariable("identifier") final String identifier) {
    return ResponseEntity.ok(this.productInstanceService.findByAccountIdentifier(identifier)
        .orElseThrow(() -> ServiceException.notFound("Product instance {0} not found.", identifier))
    );
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
          value = "/{identifier}/statement",
          method = RequestMethod.GET,
          consumes = MediaType.ALL_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<List<StatementResponse>> statement(@PathVariable("identifier") final String identifier,
                                                    @RequestParam("fromDate")
                                            @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                                    @RequestParam("toDate")
                                            @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate
                                            ) {
    LocalDateTime fromDateTime = convertToLocalDateTime(fromDate);
    LocalDateTime toDateTime = convertToLocalDateTime(toDate);

    List<StatementResponse> statement= transactionService.fetchStatement(identifier, fromDateTime, toDateTime);

    return ResponseEntity.ok(statement);
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
  @RequestMapping(
          value = "/{identifier}/balance",
          method = RequestMethod.GET,
          consumes = MediaType.ALL_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  ResponseEntity<BalanceResponse> balance(@PathVariable("identifier") final String identifier) {
    return ResponseEntity.ok(transactionService.fetchBalance(identifier));
  }

  private  LocalDateTime convertToLocalDateTime(Date dateToConvert) {
    return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
  }
}
