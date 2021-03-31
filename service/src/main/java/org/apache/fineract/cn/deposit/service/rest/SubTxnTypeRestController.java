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

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.deposit.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.SubTransactionType;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.CreateSubTxnTypeCommand;
import org.apache.fineract.cn.deposit.service.internal.command.UpdateSubTxnTypeCommand;
import org.apache.fineract.cn.deposit.service.internal.service.SubTxnTypesService;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subtxntype")
public class SubTxnTypeRestController {
    private final Logger logger;
    private final CommandGateway commandGateway;
    private final SubTxnTypesService service;

    @Autowired
    public SubTxnTypeRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                    CommandGateway commandGateway,
                                    SubTxnTypesService service) {
        this.logger = logger;
        this.commandGateway = commandGateway;
        this.service = service;
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> create(@RequestBody @Valid final SubTransactionType subTransactionType) {

        if (this.service.subTxnTypeExists(subTransactionType.getIdentifier())) {
            throw ServiceException.conflict("Sub Txn Type {0} already exists.", subTransactionType.getIdentifier());
        }

        this.service.ledgerExists(subTransactionType);

        this.commandGateway.process(new CreateSubTxnTypeCommand(subTransactionType));
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<List<SubTransactionType>> fetchSubTxnTypes() {
        return ResponseEntity.ok(this.service.findAll());
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
    @RequestMapping(
            value = "/{identifier}",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<SubTransactionType> fetchOne(@PathVariable("identifier") final String identifier) {
        Optional<SubTransactionType> optSubTxnType = service.findByIdentifier(identifier);
        if (!optSubTxnType.isPresent()) {
            throw ServiceException.notFound("Sub transaction type {0} not found.", identifier);
        }
        return ResponseEntity.ok(optSubTxnType.get());
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION_MANAGEMENT)
    @RequestMapping(
            value = "/{identifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> updateCustomer(@PathVariable("identifier") final String identifier,
                                        @RequestBody final SubTransactionType subTransactionType) {
        if (this.service.subTxnTypeExists(identifier)) {
            this.commandGateway.process(new UpdateSubTxnTypeCommand(subTransactionType));
        } else {
            throw ServiceException.notFound("Sub transaction type {0} not found.", identifier);
        }
        return ResponseEntity.accepted().build();
    }

}
