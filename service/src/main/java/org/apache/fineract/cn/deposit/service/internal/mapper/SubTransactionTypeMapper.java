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
package org.apache.fineract.cn.deposit.service.internal.mapper;

import org.apache.fineract.cn.deposit.api.v1.instance.domain.SubTransactionType;
import org.apache.fineract.cn.deposit.service.internal.repository.SubTransactionTypeEntity;

public class SubTransactionTypeMapper {

    public SubTransactionTypeMapper() {
    }

    public static SubTransactionTypeEntity map(SubTransactionType subTransactionType){
        SubTransactionTypeEntity subTransactionTypeEntity = new SubTransactionTypeEntity();
        subTransactionTypeEntity.setIdentifier(subTransactionType.getIdentifier());
        subTransactionTypeEntity.setName(subTransactionType.getName());
        subTransactionTypeEntity.setDescription(subTransactionType.getDescription());
        subTransactionTypeEntity.setCashPayment(subTransactionType.getCashPayment());
        subTransactionTypeEntity.setActive(subTransactionType.getActive());
        subTransactionTypeEntity.setOrderPosition(subTransactionType.getOrderPosition());
        subTransactionTypeEntity.setTranType(subTransactionType.getTranType());
        subTransactionTypeEntity.setLedgerAccount(subTransactionType.getLedgerAccount());
        return subTransactionTypeEntity;
    }

    public static SubTransactionType map(SubTransactionTypeEntity subTransactionTypeEntity) {
        SubTransactionType subTransactionType = new SubTransactionType();
        subTransactionType.setIdentifier(subTransactionTypeEntity.getIdentifier());
        subTransactionType.setName(subTransactionTypeEntity.getName());
        subTransactionType.setDescription(subTransactionTypeEntity.getDescription());
        subTransactionType.setCashPayment(subTransactionTypeEntity.getCashPayment());
        subTransactionType.setActive(subTransactionTypeEntity.getActive());
        subTransactionType.setOrderPosition(subTransactionTypeEntity.getOrderPosition());
        subTransactionType.setTranType(subTransactionTypeEntity.getTranType());
        subTransactionType.setLedgerAccount(subTransactionTypeEntity.getLedgerAccount());
        return subTransactionType;
    }
    public static void update(SubTransactionTypeEntity subTransactionTypeEntity, SubTransactionType subTransactionType){
        if(subTransactionType.getName() != null)
            subTransactionTypeEntity.setName(subTransactionType.getName());
        if(subTransactionType.getDescription() != null)
            subTransactionTypeEntity.setDescription(subTransactionType.getDescription());
        if(subTransactionType.getCashPayment() != null)
            subTransactionTypeEntity.setCashPayment(subTransactionType.getCashPayment());
        if(subTransactionType.getActive() != null)
            subTransactionTypeEntity.setActive(subTransactionType.getActive());
        if(subTransactionType.getOrderPosition() != null)
            subTransactionTypeEntity.setOrderPosition(subTransactionType.getOrderPosition());
        if(subTransactionType.getTranType() != null)
            subTransactionTypeEntity.setTranType(subTransactionType.getTranType());
        if(subTransactionType.getLedgerAccount() != null)
            subTransactionTypeEntity.setLedgerAccount(subTransactionType.getLedgerAccount());
    }
}
