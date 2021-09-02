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
import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.*;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.MoneyData;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.TransactionActionType;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.TransactionRequestData;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.CreateCollectionsCommand;
import org.apache.fineract.cn.deposit.service.internal.command.SettleCollectionsCommand;
import org.apache.fineract.cn.deposit.service.internal.command.UpdateCollectionsCommand;
import org.apache.fineract.cn.deposit.service.internal.mapper.CollectionsMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.*;
import org.apache.fineract.cn.deposit.service.internal.service.SelfExpiringTokenService;
import org.apache.fineract.cn.deposit.service.rest.TransactionRestController;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
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
    private final IndividualCollectionsRepository individualCollectionsRepository;
    private final SelfExpiringTokenService selfExpiringTokenService;
    private final TransactionRestController transactionRestController;

    @Autowired
    public CollectionsCommandHandler(@Qualifier(ServiceConstants.LOGGER_NAME) Logger logger,
                                     ProductInstanceRepository productInstanceRepository,
                                     SubTransactionTypeRepository subTransactionTypeRepository,
                                     CollectionsRepository collectionsRepository,
                                     IndividualCollectionsRepository individualCollectionsRepository,
                                     SelfExpiringTokenService selfExpiringTokenService,
                                     @Lazy TransactionRestController transactionRestController) {
        this.logger = logger;
        this.productInstanceRepository = productInstanceRepository;
        this.subTransactionTypeRepository = subTransactionTypeRepository;
        this.collectionsRepository = collectionsRepository;
        this.individualCollectionsRepository = individualCollectionsRepository;
        this.selfExpiringTokenService = selfExpiringTokenService;
        this.transactionRestController = transactionRestController;
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

    @Transactional
    @CommandHandler(logStart = CommandLogLevel.INFO, logFinish = CommandLogLevel.INFO)
    public CollectionsResponse settleCollection(@NotNull SettleCollectionsCommand command) throws Throwable {
        SettleCollectionsRequest request = command.getSettleCollectionsRequest();
        //1. find which collection it is
        SelfExpiringTokenEntity token = this.selfExpiringTokenService.fetchActiveToken(request.getToken());
        CollectionsEntity collectionsEntity;
        if(TokenEntities.COLLECTION.name().equals(token.getEntityType())){
            //if group collection, deposit individual, then transfer to group

            collectionsEntity = this.collectionsRepository.findByCollectionReference(token.getEntityReference())
                    .orElseThrow(() -> ServiceException.notFound("Collection {0} not found", token.getEntityReference()));
            //validate amount
            if(collectionsEntity.getAmount().compareTo(request.getAmount()) !=0){
                throw ServiceException.internalError("Amount mismatch");
            }
            if(CollectionStatusEnum.COLLECTED.name().equals(collectionsEntity.getStatus())){
                throw ServiceException.internalError("Already Collected");
            }


            for (IndividualCollectionsEntity individualCollectionsEntity : collectionsEntity.getIndvCollections()) {
                if (individualCollectionsEntity.getAttendance().equals(AttendanceEnum.PRESENT.name())) {
                    //deposit
                    TransactionRequestData depositReq = new TransactionRequestData(request.getTransactionCode(), request.getRequestCode(),
                            request.getRoutingCode(), request.getExternalId(), individualCollectionsEntity.getAccount().getAccountIdentifier(),
                            request.getNote(), null,
                            MoneyData.build(individualCollectionsEntity.getAmount(), individualCollectionsEntity.getCollection().getCurrency()),
                            collectionsEntity.getSubTxnType().getIdentifier(), null, null, request.getTxnDate());

                    transactionRestController.performTxn(TransactionActionType.DEPOSIT.name(), depositReq);

                    //transfer
                    TransactionRequestData transferReq = new TransactionRequestData(request.getTransactionCode(), request.getRequestCode(),
                            request.getRoutingCode(), request.getExternalId(), null,
                            request.getNote(), null,
                            MoneyData.build(individualCollectionsEntity.getAmount(), individualCollectionsEntity.getCollection().getCurrency()),
                            collectionsEntity.getSubTxnType().getIdentifier(), individualCollectionsEntity.getAccount().getAccountIdentifier(), collectionsEntity.getAccount().getAccountIdentifier(),
                            request.getTxnDate());

                    transactionRestController.performTxn(TransactionActionType.TRANSFER.name(), transferReq);

                    this.selfExpiringTokenService.markTokenAsUsed(individualCollectionsEntity.getToken());
                }
            }
            this.selfExpiringTokenService.markTokenAsUsed(collectionsEntity.getToken());
            collectionsEntity.setStatus(CollectionStatusEnum.COLLECTED.name());
            collectionsRepository.save(collectionsEntity);
        }else{
            //if individual collection, deposit to individual account
            IndividualCollectionsEntity individualCollectionsEntity = this.individualCollectionsRepository.findByIndividualCollectionReference(token.getEntityReference())
                    .orElseThrow(() -> ServiceException.notFound("Collection {0} not found", token.getEntityReference()));
            collectionsEntity = individualCollectionsEntity.getCollection();
            //validate amount
            if(individualCollectionsEntity.getAmount().compareTo(request.getAmount()) != 0){
                throw ServiceException.internalError("Amount mismatch");
            }
            if(CollectionStatusEnum.COLLECTED.name().equals(collectionsEntity.getStatus())){
                throw ServiceException.internalError("Already Collected");
            }
            //add txn Date and subTxnType
            //deposit
            TransactionRequestData depositReq = new TransactionRequestData(request.getTransactionCode(), request.getRequestCode(),
                    request.getRoutingCode(), request.getExternalId(), individualCollectionsEntity.getAccount().getAccountIdentifier(),
                    request.getNote(), null,
                    MoneyData.build(individualCollectionsEntity.getAmount(), individualCollectionsEntity.getCollection().getCurrency()),
                    collectionsEntity.getSubTxnType().getIdentifier(), null, null, request.getTxnDate());

            transactionRestController.performTxn(TransactionActionType.DEPOSIT.name(), depositReq);
            this.selfExpiringTokenService.markTokenAsUsed(individualCollectionsEntity.getToken());
            collectionsEntity.setStatus(CollectionStatusEnum.PROCESSING.name());
            collectionsRepository.save(collectionsEntity);
        }
        return CollectionsMapper.map(collectionsEntity);
    }
}
