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

package org.apache.fineract.cn.deposit.service.internal.command.handler;

import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.CommandLogLevel;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.CollectionsResponse;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.IndividualPayments;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.TokenEntities;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.CreateCollectionsCommand;
import org.apache.fineract.cn.deposit.service.internal.command.UpdateCollectionsCommand;
import org.apache.fineract.cn.deposit.service.internal.mapper.CollectionsMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.*;
import org.apache.fineract.cn.deposit.service.internal.service.SelfExpiringTokenService;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Aggregate
public class CollectionsCommandHandler {
    private final Logger logger;
    private final ProductInstanceRepository productInstanceRepository;
    private final SubTransactionTypeRepository subTransactionTypeRepository;
    private final CollectionsRepository collectionsRepository;
    private final SelfExpiringTokenService selfExpiringTokenService;

    @Autowired
    public CollectionsCommandHandler(@Qualifier(ServiceConstants.LOGGER_NAME) Logger logger,
                                     ProductInstanceRepository productInstanceRepository,
                                     SubTransactionTypeRepository subTransactionTypeRepository,
                                     CollectionsRepository collectionsRepository,
                                     SelfExpiringTokenService selfExpiringTokenService) {
        this.logger = logger;
        this.productInstanceRepository = productInstanceRepository;
        this.subTransactionTypeRepository = subTransactionTypeRepository;
        this.collectionsRepository = collectionsRepository;
        this.selfExpiringTokenService = selfExpiringTokenService;
    }

    @Transactional
    @CommandHandler(logStart = CommandLogLevel.INFO, logFinish = CommandLogLevel.INFO)
    public CollectionsResponse saveCollection(@NotNull CreateCollectionsCommand command) {

        CollectionsEntity collectionsEntity = CollectionsMapper.map(command.getCollectionRequest(),
                productInstanceRepository, subTransactionTypeRepository);

        //create self expiring tokens
        LocalDateTime currentTime =  getNow();
        collectionsEntity.setToken(this.selfExpiringTokenService.generateAndSaveToken(TokenEntities.COLLECTION.name(), collectionsEntity.getCollectionReference(), currentTime));

        collectionsEntity.getIndvCollections().forEach(
                x-> {
                    SelfExpiringTokenEntity token = selfExpiringTokenService.generateAndSaveToken(TokenEntities.INDV_COLLECTION.name(), x.getIndividualCollectionReference(), currentTime);
                    x.setToken(token);
                }
        );

        //save collections
        collectionsRepository.save(collectionsEntity);

        return CollectionsMapper.map(collectionsEntity);
    }

    @Transactional
    @CommandHandler(logStart = CommandLogLevel.INFO, logFinish = CommandLogLevel.INFO)
    public CollectionsResponse updateCollection(@NotNull UpdateCollectionsCommand command) {

        CollectionsEntity collectionsEntity = this.collectionsRepository.findByCollectionReference(command.getCollectionsReference())
                .orElseThrow(() -> ServiceException.notFound("Collection {0} not found", command.getCollectionsReference()));

        collectionsEntity.setAmount(BigDecimal.ZERO);
        collectionsEntity.getIndvCollections().forEach(x->{
                Optional<IndividualPayments> individualPaymentsOptional =  command.getCollectionRequest().getIndividualPayments().stream().filter(a -> x.getAccount().getAccountIdentifier().equals(a.getAccountNumber())).findFirst();
                if(individualPaymentsOptional.isPresent()){
                    IndividualPayments individualPayment = individualPaymentsOptional.get();
                    x.setAttendance(individualPayment.getAttendance());
                    if(individualPayment.getAmount() != null) {
                        x.setAmount(individualPayment.getAmount());
                    }
                    collectionsEntity.setAmount(collectionsEntity.getAmount().add(x.getAmount()));
                }
            }
        );

        //save collections
        collectionsRepository.save(collectionsEntity);

        return CollectionsMapper.map(collectionsEntity);
    }

    private static LocalDateTime getNow() {
        return LocalDateTime.now(Clock.systemUTC());
    }


}
