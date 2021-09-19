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

package org.apache.fineract.cn.deposit.api.v1.collection.domain.data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CollectionsRequest {
    private String txnDate;
    @NotNull
    private BigDecimal amount;
    private String currency;
    private String remarks;
    @NotNull
    private String accountId;
    private String subtxnId;
    private BigDecimal fee;
    private List<IndividualPayments> individualPayments;
    private String reference;

    public CollectionsRequest() {
    }

    public CollectionsRequest(String txnDate, BigDecimal amount, String currency, String remarks,
                              String accountId, String subtxnId, BigDecimal fee,
                              List<IndividualPayments> individualPayments, String reference) {
        this.txnDate = txnDate;
        this.amount = amount;
        this.currency = currency;
        this.remarks = remarks;
        this.accountId = accountId;
        this.subtxnId = subtxnId;
        this.fee = fee;
        this.individualPayments = individualPayments;
        this.reference = reference;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSubtxnId() {
        return subtxnId;
    }

    public void setSubtxnId(String subtxnId) {
        this.subtxnId = subtxnId;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public List<IndividualPayments> getIndividualPayments() {
        return individualPayments;
    }

    public void setIndividualPayments(List<IndividualPayments> individualPayments) {
        this.individualPayments = individualPayments;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
