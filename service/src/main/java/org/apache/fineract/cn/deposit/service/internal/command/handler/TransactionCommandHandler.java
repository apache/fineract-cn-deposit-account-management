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

import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.CommandLogLevel;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.TransactionResponseData;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.TransactionCommand;
import org.apache.fineract.cn.deposit.service.internal.service.TransactionService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Aggregate
public class TransactionCommandHandler {
    private final Logger logger;
    private final TransactionService transactionService;

    @Autowired
    public TransactionCommandHandler(@Qualifier(ServiceConstants.LOGGER_NAME)Logger logger,
                                     TransactionService transactionService) {
        this.logger = logger;
        this.transactionService = transactionService;
    }

    @NotNull
    @Transactional
    @CommandHandler(logStart = CommandLogLevel.INFO, logFinish = CommandLogLevel.INFO)
    @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_TXN)
    public TransactionResponseData performTransfer(@NotNull TransactionCommand command) {

        switch (command.getAction()) {
            case WITHDRAWAL: {
                //command = dataValidator.validatePrepareTransfer(command);
                return transactionService.withdraw(command);
            }
            case DEPOSIT: {
                //command = dataValidator.validateCommitTransfer(command);
                return transactionService.deposit(command);
            }
            default:
                return null;
        }
    }

}
