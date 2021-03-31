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

import org.apache.fineract.cn.accounting.api.v1.client.LedgerManager;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.SubTransactionType;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.CreateSubTxnTypeCommand;
import org.apache.fineract.cn.deposit.service.internal.command.UpdateSubTxnTypeCommand;
import org.apache.fineract.cn.deposit.service.internal.mapper.SubTransactionTypeMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.SubTransactionTypeEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.SubTransactionTypeRepository;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

@Aggregate
public class SubTxnTypeAggrigate {
    private final Logger logger;
    private final LedgerManager ledgerManager;
    private final SubTransactionTypeRepository subTransactionTypeRepository;

    @Autowired
    public SubTxnTypeAggrigate(@Qualifier(ServiceConstants.LOGGER_NAME) Logger logger,
                               LedgerManager ledgerManager,
                               SubTransactionTypeRepository subTransactionTypeRepository) {
        this.logger = logger;
        this.ledgerManager = ledgerManager;
        this.subTransactionTypeRepository = subTransactionTypeRepository;
    }

    @CommandHandler
    @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_SUB_TXN_TYPE)
    @Transactional
    public String createProductInstance(final CreateSubTxnTypeCommand createSubTxnTypeCommand) {
        final SubTransactionType subTransactionType = createSubTxnTypeCommand.subTransactionType();

        final SubTransactionTypeEntity subTransactionTypeEntity = SubTransactionTypeMapper.map(subTransactionType);
        this.subTransactionTypeRepository.save(subTransactionTypeEntity);
        return subTransactionTypeEntity.getIdentifier();
    }

    @CommandHandler
    @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.PUT_SUB_TXN_TYPE)
    @Transactional
    public String updateProductInstance(final UpdateSubTxnTypeCommand createSubTxnTypeCommand) {
        final SubTransactionType subTransactionType = createSubTxnTypeCommand.subTransactionType();

        final SubTransactionTypeEntity subTransactionTypeEntity = subTransactionTypeRepository.findByIdentifier(subTransactionType.getIdentifier())
                .orElseThrow(() ->
                        ServiceException.notFound("Sub transaction type {0} not found.", subTransactionType.getIdentifier()));
        SubTransactionTypeMapper.update(subTransactionTypeEntity, subTransactionType);
        this.subTransactionTypeRepository.save(subTransactionTypeEntity);
        return subTransactionTypeEntity.getIdentifier();
    }
}
