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

package org.apache.fineract.cn.deposit.service.internal.repository;

import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.postgresql.util.LocalDateTimeConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "shed_collections")
public class CollectionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "transaction_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime transactionDate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transport_fee_amount", nullable = false)
    private BigDecimal transportFeeAmount;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "remarks", length = 1024)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_identifier", nullable = false, unique = true, referencedColumnName = "account_identifier")
    private ProductInstanceEntity account;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sub_txn_type_id", nullable = false, unique = true, referencedColumnName = "identifier")
    private SubTransactionTypeEntity subTxnType;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "c_reference", length = 36)
    private String collectionReference;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "collections_id") // we need to duplicate the physical information
    private Set<IndividualCollectionsEntity> indvCollections;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "token", nullable = false, unique = true)
    private SelfExpiringTokenEntity token;

    public CollectionsEntity() {
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTransportFeeAmount() {
        return transportFeeAmount;
    }

    public void setTransportFeeAmount(BigDecimal transportFeeAmount) {
        this.transportFeeAmount = transportFeeAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ProductInstanceEntity getAccount() {
        return account;
    }

    public void setAccount(ProductInstanceEntity account) {
        this.account = account;
    }

    public SubTransactionTypeEntity getSubTxnType() {
        return subTxnType;
    }

    public void setSubTxnType(SubTransactionTypeEntity subTxnType) {
        this.subTxnType = subTxnType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCollectionReference() {
        return collectionReference;
    }

    public void setCollectionReference(String collectionReference) {
        this.collectionReference = collectionReference;
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

    public Set<IndividualCollectionsEntity> getIndvCollections() {
        return indvCollections;
    }

    public void addToIndvCollections(IndividualCollectionsEntity indvCollection) {
        this.indvCollections.add(indvCollection);
    }

    public void setIndvCollections(Set<IndividualCollectionsEntity> indvCollections) {
        this.indvCollections = indvCollections;
    }

    public SelfExpiringTokenEntity getToken() {
        return token;
    }

    public void setToken(SelfExpiringTokenEntity token) {
        this.token = token;
    }
}
