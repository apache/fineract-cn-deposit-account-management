/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fineract.cn.deposit.service.internal.mapper;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.AttendanceEnum;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.CollectionsRequest;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.CollectionsResponse;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.IndividualPayments;
import org.apache.fineract.cn.deposit.service.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectionsMapper {
    private CollectionsMapper() {
        super();
    }
    
    public static CollectionsEntity map(CollectionsRequest collectionsRequest,
                                        ProductInstanceRepository accountRepository,
                                        SubTransactionTypeRepository subTxnRepository){
        CollectionsEntity  entity = new CollectionsEntity();

        if(collectionsRequest.getTxnDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMMM-dd");
            entity.setTransactionDate(LocalDate.parse(collectionsRequest.getTxnDate(), formatter).atStartOfDay());
        }else {
            entity.setTransactionDate(getNow());
        }
        entity.setAmount(BigDecimal.ZERO);
        entity.setTransportFeeAmount(collectionsRequest.getFee());
        entity.setCurrency(collectionsRequest.getCurrency());
        entity.setRemarks(collectionsRequest.getRemarks());
        entity.setAccount(accountRepository.findByAccountIdentifier(collectionsRequest.getAccountId()).orElseThrow(() -> ServiceException.notFound("Account {0} not found.", collectionsRequest.getAccountId())));
        entity.setSubTxnType(subTxnRepository.findByIdentifier(collectionsRequest.getSubtxnId()).orElseThrow(() -> ServiceException.notFound("Sub Txn Type {0} not found.", collectionsRequest.getSubtxnId())));
        entity.setStatus("INIT");
        entity.setCollectionReference(UUID.randomUUID().toString());
        entity.setCreatedBy(getLoginUser());
        entity.setCreatedOn(getNow());
        entity.setLastModifiedBy(getLoginUser());
        entity.setLastModifiedOn(getNow());
        entity.setIndvCollections(collectionsRequest.getIndividualPayments().stream().map(x-> {
            IndividualCollectionsEntity indv = new IndividualCollectionsEntity();
            indv.setCollection(entity);
            indv.setAccount(accountRepository.findByAccountIdentifier(x.getAccountNumber()).orElseThrow(() -> ServiceException.notFound("Account {0} not found.", x.getAccountNumber())));
            indv.setAccountExternalId(x.getAccountNumber());
            indv.setAmount(x.getAmount());
            indv.setIndividualCollectionReference(UUID.randomUUID().toString());
            entity.setAmount(entity.getAmount().add(x.getAmount()));
            return indv;
        }).collect(Collectors.toSet()));
        return entity;
    }

    public static CollectionsResponse map(CollectionsEntity collectionsEntity){
        return new CollectionsResponse(collectionsEntity.getCollectionReference(),
                collectionsEntity.getToken().getToken(),
                collectionsEntity.getToken().getTokenExpiresBy(),
                collectionsEntity.getIndvCollections().stream().map( x ->{
                    return new IndividualPayments(x.getAccount().getAccountIdentifier(),
                            x.getAttendance() == null ? null: AttendanceEnum.valueOf(x.getAttendance()),
                            x.getIndividualCollectionReference(), x.getAmount(),
                            x.getToken().getToken(), x.getToken().getTokenExpiresBy());
                }).collect(Collectors.toList()));
    }

    private static LocalDateTime getNow() {
        return LocalDateTime.now(Clock.systemUTC());
    }
    private static String getLoginUser() {
        return UserContextHolder.checkedGetUser();
    }
}
