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

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shed_collections_inidividual")
public class IndividualCollectionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "collections_id", nullable = false, unique = true)
    private CollectionsEntity collection;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_identifier", nullable = false, unique = true, referencedColumnName = "account_identifier")
    private ProductInstanceEntity account;

    @Column(name = "account_external_id", length = 64)
    private String accountExternalId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "i_reference", length = 36)
    private String individualCollectionReference;

    @Column(name = "attendance", length = 10)
    private String attendance;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "token", nullable = false, unique = true)
    private SelfExpiringTokenEntity token;

    public IndividualCollectionsEntity() {
    }

    public CollectionsEntity getCollection() {
        return collection;
    }

    public void setCollection(CollectionsEntity collection) {
        this.collection = collection;
    }

    public ProductInstanceEntity getAccount() {
        return account;
    }

    public void setAccount(ProductInstanceEntity account) {
        this.account = account;
    }

    public String getAccountExternalId() {
        return accountExternalId;
    }

    public void setAccountExternalId(String accountExternalId) {
        this.accountExternalId = accountExternalId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getIndividualCollectionReference() {
        return individualCollectionReference;
    }

    public void setIndividualCollectionReference(String individualCollectionReference) {
        this.individualCollectionReference = individualCollectionReference;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public SelfExpiringTokenEntity getToken() {
        return token;
    }

    public void setToken(SelfExpiringTokenEntity token) {
        this.token = token;
    }
}
