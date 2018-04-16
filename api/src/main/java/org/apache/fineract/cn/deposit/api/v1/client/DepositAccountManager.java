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
package org.apache.fineract.cn.deposit.api.v1.client;

import org.apache.fineract.cn.deposit.api.v1.definition.ActionAlreadyExistsException;
import org.apache.fineract.cn.deposit.api.v1.definition.ProductDefinitionAlreadyExistsException;
import org.apache.fineract.cn.deposit.api.v1.definition.ProductDefinitionNotFoundException;
import org.apache.fineract.cn.deposit.api.v1.definition.ProductDefinitionValidationException;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Action;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.DividendDistribution;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.api.v1.instance.ProductInstanceNotFoundException;
import org.apache.fineract.cn.deposit.api.v1.instance.ProductInstanceValidationException;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.AvailableTransactionType;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.apache.fineract.cn.api.annotation.ThrowsException;
import org.apache.fineract.cn.api.annotation.ThrowsExceptions;
import org.apache.fineract.cn.api.util.CustomFeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings("unused")
@FeignClient(value = "deposit-v1", path = "/deposit/v1", configuration = CustomFeignClientsConfiguration.class)
public interface DepositAccountManager {

  @RequestMapping(
      value = "/actions",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions(
      @ThrowsException(status = HttpStatus.CONFLICT, exception = ActionAlreadyExistsException.class)
  )
  void create(@RequestBody @Valid final Action action);

  @RequestMapping(
      value = "/actions",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  List<Action> fetchActions();

  @RequestMapping(
      value = "/definitions",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions(
      @ThrowsException(status = HttpStatus.CONFLICT, exception = ProductDefinitionAlreadyExistsException.class)
  )
  void create(@RequestBody @Valid final ProductDefinition productDefinition);

  @RequestMapping(
      value = "/definitions",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  List<ProductDefinition> fetchProductDefinitions();

  @RequestMapping(
      value = "/definitions/{identifier}",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  ProductDefinition findProductDefinition(@PathVariable("identifier") final String Identifier);

  @RequestMapping(
      value = "/definitions/{identifier}/instances",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  List<ProductInstance> findProductInstances(@PathVariable("identifier") final String Identifier);

  @RequestMapping(
      value = "/definitions/{identifier}/commands",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  void process(@PathVariable("identifier") final String identifier,
               @RequestBody @Valid final ProductDefinitionCommand command);

  @RequestMapping(
      value = "/definitions/{identifier}/commands",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  List<ProductDefinitionCommand> getProductDefinitionCommands(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/instances",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  void create(@RequestBody @Valid final ProductInstance productInstance);

  @RequestMapping(
      value = "/instances",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  List<ProductInstance> fetchProductInstances(@RequestParam(value = "customer", required = true) final String customer);

  @RequestMapping(
      value = "/instances/transactiontypes",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ResponseBody
  Set<AvailableTransactionType> fetchPossibleTransactionTypes(
      @RequestParam(value = "customer", required = true) final String customer
  );

  @RequestMapping(
      value = "/instances/{identifier}",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  void postProductInstanceCommand(@PathVariable("identifier") final String identifier,
                                  @RequestParam(value = "command", required = true) final String command);

  @RequestMapping(
      value = "/definitions/{identifier}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ProductDefinitionNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = ProductDefinitionValidationException.class)
  })
  void changeProductDefinition(@PathVariable("identifier") final String Identifier,
                               @RequestBody @Valid ProductDefinition productDefinition);

  @RequestMapping(
      value = "/definitions/{identifier}",
      method = RequestMethod.DELETE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ProductDefinitionNotFoundException.class),
      @ThrowsException(status = HttpStatus.CONFLICT, exception = ProductDefinitionValidationException.class)
  })
  void deleteProductDefinition(@PathVariable("identifier") final String Identifier);

  @RequestMapping(
      value = "/instances/{identifier}",
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ProductInstanceNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = ProductInstanceValidationException.class)
  })
  void changeProductInstance(@PathVariable("identifier") final String identifier,
                             @RequestBody @Valid final ProductInstance productInstance);

  @RequestMapping(
      value = "/instances/{identifier}",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ProductInstanceNotFoundException.class)
  })
  ProductInstance findProductInstance(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/definitions/{identifier}/dividends",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ProductDefinitionNotFoundException.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = ProductDefinitionValidationException.class)
  })
  void dividendDistribution(@PathVariable("identifier") final String identifier,
                            @RequestBody @Valid final DividendDistribution dividendDistribution);

  @RequestMapping(
      value = "/definitions/{identifier}/dividends",
      method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.ALL_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ProductDefinitionNotFoundException.class),
  })
  List<DividendDistribution> fetchDividendDistributions(@PathVariable("identifier") final String identifier);
}
