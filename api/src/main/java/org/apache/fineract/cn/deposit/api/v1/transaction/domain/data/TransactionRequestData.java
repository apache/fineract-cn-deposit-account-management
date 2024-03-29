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
package org.apache.fineract.cn.deposit.api.v1.transaction.domain.data;

import org.apache.fineract.cn.deposit.api.v1.definition.domain.Currency;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.beans.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionRequestData {

    public static final String IDENTIFIER_SEPARATOR = "_";

    @NotNull
    //@Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    private String transactionCode;

    //@Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    private String requestCode;

    private String routingCode;
    private String externalId;

    @NotEmpty
    @Length(max = 32)
    private String accountId;

    @Length(max = 128)
    private String note;

    private LocalDateTime expiration;

    private MoneyData amount;
    //private GeoCodeData geoCode;

    private  String subTxnId;

    @Length(max = 32)
    private String fromAccountId;

    @Length(max = 32)
    private String toAccountId;

    public TransactionRequestData() {
    }

    public TransactionRequestData(String transactionCode, String requestCode, String routingCode, String externalId, String accountId,
                                  String note, LocalDateTime expiration,
                                  MoneyData amount, String subTxnId,
                                  String fromAccountId, String toAccountId) {
        this.transactionCode = transactionCode;
        this.requestCode = requestCode;
        this.routingCode = routingCode;
        this.externalId = externalId;
        this.accountId = accountId;
        this.note = note;
        this.expiration = expiration;
        this.amount = amount;
        //this.geoCode = geoCode;
        this.subTxnId = subTxnId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    @NotNull
    public String getTransactionCode() {
        return transactionCode;
    }

    public String getRequestCode() {
        return requestCode;
    }

    @NotNull
    public String getAccountId() {
        return accountId;
    }

    @NotNull
    public MoneyData getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public LocalDate getExpirationLocalDate() {
        return expiration == null ? null : expiration.toLocalDate();
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public void normalizeAmounts(@NotNull Currency currency) {
        amount.normalizeAmount(currency);
    }

    protected void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    protected void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    protected void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    protected void setAmount(MoneyData amount) {
        this.amount = amount;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getSubTxnId() {
        return subTxnId;
    }

    @Transient
    @NotNull
    public String getIdentifier() {
        return transactionCode;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }
}
