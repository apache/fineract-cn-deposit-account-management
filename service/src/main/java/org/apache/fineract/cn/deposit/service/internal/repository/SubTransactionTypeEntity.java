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

import javax.persistence.*;

@Entity
@Table(name = "shed_sub_tx_type")
public class SubTransactionTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "a_name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_cash_payment", nullable = false)
    private Boolean isCashPayment;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "order_position", nullable = false)
    private Integer orderPosition;

    @Column(name = "tran_type_enum", nullable = false)
    private Integer tranType;

    @Column(name = "ledger_account_identifier", nullable = false)
    private String ledgerAccount;

    public SubTransactionTypeEntity() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getCashPayment() {
        return isCashPayment;
    }

    public void setCashPayment(Boolean cashPayment) {
        isCashPayment = cashPayment;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    public Integer getTranType() {
        return tranType;
    }

    public void setTranType(Integer tranType) {
        this.tranType = tranType;
    }

    public String getLedgerAccount() {
        return ledgerAccount;
    }

    public void setLedgerAccount(String ledgerAccount) {
        this.ledgerAccount = ledgerAccount;
    }
}
