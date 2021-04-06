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
package org.apache.fineract.cn.deposit.service.internal.repository;

import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.ActionState;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.TransactionTypeEnum;
import org.apache.fineract.cn.postgresql.util.LocalDateTimeConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shed_transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "identifier", nullable = false, length = 36)
    private String identifier;

    @Column(name = "account_identifier", length = 36)
    private String accountId;

    @Column(name = "routing_code", length = 256)
    private String routingCode;

    @Column(name = "external_id", length = 256)
    private String externalId;

    @Column(name = "a_name", length = 256)
    private String name;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "transaction_type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column(name = "sub_txn_type", length = 36)
    private String subTxnType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;


    @Column(name = "fee_amount")
    private BigDecimal feeAmount;

    @Column(name = "state", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private ActionState state;

    @Column(name = "customer_account_identifier", nullable = false, length = 32)
    private String customerAccountIdentifier;

    @Column(name = "payable_account_identifier", length = 32)
    private String prepareAccountIdentifier;

    @Column(name = "nostro_account_identifier", length = 32)
    private String nostroAccountIdentifier;

    @Column(name = "transaction_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime transactionDate;

    @Column(name = "expiration_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime expirationDate;

    @Column(name = "created_by", nullable = false, length = 32)
    private String createdBy;

    @Column(name = "created_on", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdOn;

    @Column(name = "last_modified_by", length = 32)
    private String lastModifiedBy;

    @Column(name = "last_modified_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastModifiedOn;

    @Column(name = "a_type", length = 32)
    private String type;

    @ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_txn_id", nullable = false)
    private TransactionEntity parentTransaction;

    public TransactionEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionTypeEnum getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ActionState getState() {
        return state;
    }

    public void setState(ActionState state) {
        this.state = state;
    }

    public String getCustomerAccountIdentifier() {
        return customerAccountIdentifier;
    }

    public void setCustomerAccountIdentifier(String customerAccountIdentifier) {
        this.customerAccountIdentifier = customerAccountIdentifier;
    }

    public String getPrepareAccountIdentifier() {
        return prepareAccountIdentifier;
    }

    public void setPrepareAccountIdentifier(String prepareAccountIdentifier) {
        this.prepareAccountIdentifier = prepareAccountIdentifier;
    }

    public String getNostroAccountIdentifier() {
        return nostroAccountIdentifier;
    }

    public void setNostroAccountIdentifier(String nostroAccountIdentifier) {
        this.nostroAccountIdentifier = nostroAccountIdentifier;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(LocalDateTime lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSubTxnType() {
        return subTxnType;
    }

    public void setSubTxnType(String subTxnType) {
        this.subTxnType = subTxnType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TransactionEntity getParentTransaction() {
        return parentTransaction;
    }

    public void setParentTransaction(TransactionEntity parentTransaction) {
        this.parentTransaction = parentTransaction;
    }
}
