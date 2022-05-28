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

import java.math.BigDecimal;

/**
 * @author manoj
 */
public class SettleCollectionsRequest {
    private String token;
    private BigDecimal amount;

    private String transactionCode;
    private String requestCode;
    private String routingCode;
    private String externalId;
    private String note;
    private String txnDate;

    public SettleCollectionsRequest() {
    }

    public SettleCollectionsRequest(String token, BigDecimal amount, String transactionCode,
                                    String requestCode, String routingCode, String externalId, String note,
                                    String txnDate) {
        this.token = token;
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.requestCode = requestCode;
        this.routingCode = routingCode;
        this.externalId = externalId;
        this.note = note;
        this.txnDate = txnDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

}
