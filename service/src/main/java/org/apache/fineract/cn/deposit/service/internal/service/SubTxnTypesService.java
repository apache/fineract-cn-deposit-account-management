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
package org.apache.fineract.cn.deposit.service.internal.service;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.cn.accounting.api.v1.client.LedgerManager;
import org.apache.fineract.cn.accounting.api.v1.domain.Account;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.SubTransactionType;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.mapper.ProductInstanceMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.SubTransactionTypeMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.SubTransactionTypeRepository;
import org.apache.fineract.cn.deposit.service.internal.service.helper.AccountingService;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubTxnTypesService {
    private final Logger logger;
    private final SubTransactionTypeRepository subTransactionTypeRepository;
    private final LedgerManager ledgerManager;

    @Autowired
    public SubTxnTypesService(@Qualifier(ServiceConstants.LOGGER_NAME) Logger logger,
                              SubTransactionTypeRepository subTransactionTypeRepository,
                              LedgerManager ledgerManager) {
        this.logger = logger;
        this.subTransactionTypeRepository = subTransactionTypeRepository;
        this.ledgerManager = ledgerManager;
    }

    public Optional<SubTransactionType> findByIdentifier(final String identifier) {
        return this.subTransactionTypeRepository.findByIdentifier(identifier).map(SubTransactionTypeMapper::map);
    }
    public List<SubTransactionType> findAll() {
        return this.subTransactionTypeRepository.findAll()
                .stream().map(SubTransactionTypeMapper::map).collect(Collectors.toList());
    }

    public Boolean subTxnTypeExists(final String identifier) {
        return this.findByIdentifier(identifier).isPresent();
    }

    public Boolean ledgerExists(SubTransactionType subTransactionType){
        if(StringUtils.isNotBlank(subTransactionType.getLedgerAccount())){
            if(this.ledgerManager.findAccount(subTransactionType.getLedgerAccount()) == null){
                throw ServiceException.conflict("Ledger Account not found.", subTransactionType.getLedgerAccount());
            }
        }
        return false;
    }

}
