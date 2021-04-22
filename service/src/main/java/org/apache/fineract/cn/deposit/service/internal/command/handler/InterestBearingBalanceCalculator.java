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
import org.apache.fineract.cn.deposit.api.v1.domain.Type;
import org.apache.fineract.cn.deposit.service.internal.command.CalculateIBBCommand;
import org.apache.fineract.cn.deposit.service.internal.repository.*;
import org.apache.fineract.cn.deposit.service.internal.service.TransactionService;
import org.apache.fineract.cn.lang.DateConverter;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Aggregate
public class InterestBearingBalanceCalculator {

    private static final String ACTIVE = "ACTIVE";

    private ProductDefinitionRepository productDefinitionRepository;
    private ProductInstanceRepository productInstanceRepository;
    private TransactionRepository transactionRepository;
    private SubTransactionTypeRepository subTransactionTypeRepository;

    //TODO: constructor

    @Transactional
    @CommandHandler(logStart = CommandLogLevel.DEBUG, logFinish =  CommandLogLevel.DEBUG)
    @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.CALCULATE_IBB)
    public String process(final CalculateIBBCommand calculateIBBCommand) {
        final LocalDate dueDate = calculateIBBCommand.dueDate();
        //calculate and store ibb for the date-1day
        final List<ProductDefinitionEntity> productDefinitions = this.productDefinitionRepository.findAll();

        productDefinitions.forEach(productDefinitionEntity -> {
            if (this.canFindIBBForProduct(productDefinitionEntity)) {
                final List<ProductInstanceEntity> productInstances =
                        this.productInstanceRepository.findByProductDefinition(productDefinitionEntity);

                productInstances.forEach(productInstanceEntity -> {
                    if (productInstanceEntity.getState().equals(ACTIVE)) {
                        //get transactions
                        List<TransactionEntity> transactions =  transactionRepository.findByAccountId(productInstanceEntity.getAccountIdentifier());
                        if(!transactions.isEmpty())
                            calculateAndSaveIBB(productInstanceEntity, transactions, dueDate);
                    }
                });
            }
        });

        return DateConverter.toIsoString(dueDate);
    }

    private void calculateAndSaveIBB(ProductInstanceEntity productInstanceEntity, List<TransactionEntity> transactions,
                                     LocalDate dueDate) {
        BigDecimal ibBalance = BigDecimal.ZERO;
        for(TransactionEntity transaction: transactions){
            SubTransactionTypeEntity subTransactionTypeEntity = getSubTxnTypeEntityFromTxn(transaction);
            if(subTransactionTypeEntity!= null && (subTransactionTypeEntity.getIbbConfPlusDays() != 0
                || subTransactionTypeEntity.getIbbConfMinusDays() !=0)) {

                if(TransactionService.CREDIT.equals(transaction.getType()) &&
                        isTxnDateOnOrAfterTodayPlusX(dueDate, transaction.getTransactionDate().toLocalDate(),
                                subTransactionTypeEntity.getIbbConfPlusDays())){
                    //add credit after checking day passed condition: Deposit Value Date - T+x
                    ibBalance = ibBalance.add(transaction.getAmount());

                }else if(TransactionService.DEBIT.equals(transaction.getType())&&
                        isTxnDateOnOrBeforeTodayMinusY(dueDate, transaction.getTransactionDate().toLocalDate(),
                                subTransactionTypeEntity.getIbbConfMinusDays())){
                    //subtract debit after checking day condition: Withdrawal Value Date - T-y
                    ibBalance = ibBalance.subtract(transaction.getAmount());
                }
                //else ignore
            }else if(TransactionService.CREDIT.equals(transaction.getType())){
                //add credit
                ibBalance = ibBalance.add(transaction.getAmount());

            }else if(TransactionService.DEBIT.equals(transaction.getType())) {
                //subtract debit
                ibBalance = ibBalance.subtract(transaction.getAmount());
            }
        }
    }

    private SubTransactionTypeEntity getSubTxnTypeEntityFromTxn(TransactionEntity transaction){
        if(transaction.getSubTxnType() == null) return null;
        Optional<SubTransactionTypeEntity> optsubTxn = subTransactionTypeRepository.findByIdentifier(transaction.getSubTxnType());
        return (optsubTxn.isPresent() ? optsubTxn.get(): null);
    }

    private boolean canFindIBBForProduct(final ProductDefinitionEntity productDefinitionEntity) {
        return productDefinitionEntity.getActive()
                && !productDefinitionEntity.getType().equals(Type.SHARE.name())
                && productDefinitionEntity.getInterest() != null
                && productDefinitionEntity.getInterest() > 0.00D;
    }

    private boolean isTxnDateOnOrAfterTodayPlusX(LocalDate today, LocalDate txnDate, int x){

        return false;
    }

    private boolean isTxnDateOnOrBeforeTodayMinusY(LocalDate today, LocalDate txnDate, int y){
        return false;
    }

}
