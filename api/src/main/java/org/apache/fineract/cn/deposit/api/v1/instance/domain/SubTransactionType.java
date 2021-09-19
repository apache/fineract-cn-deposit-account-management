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
package org.apache.fineract.cn.deposit.api.v1.instance.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class SubTransactionType {
    @NotBlank
    private String identifier;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Boolean cashPayment;
    @NotNull
    private Boolean active;
    private Integer orderPosition;
    private Integer tranType;
    private String ledgerAccount;

    public SubTransactionType() {
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
        return cashPayment;
    }

    public void setCashPayment(Boolean cashPayment) {
        this.cashPayment = cashPayment;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
