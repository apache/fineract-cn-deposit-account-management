/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.deposit.api.v1.transaction.domain.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionResponseData {


    @NotNull
    private final String transactionCode;

    private final String routingCode;
    private final String externalId;

    @NotNull
    private final ActionState state;

    private final String expiration;



    private String completedTimestamp;


    @NotNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    private final String requestCode;

    public TransactionResponseData(String transactionCode, String routingCode, String externalId,
                                   ActionState state, String expiration,
                                   String requestCode,
                                   String completedTimestamp) {
        this.transactionCode = transactionCode;
        this.routingCode = routingCode;
        this.externalId = externalId;
        this.state = state;
        this.expiration = expiration;
        this.requestCode = requestCode;
        this.completedTimestamp =completedTimestamp;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public ActionState getState() {
        return state;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public static TransactionResponseData build(String routingCode, String externalId, @NotNull String requestCode,
                                                    @NotNull ActionState state,
                                                    LocalDateTime expiration,
                                                    @NotNull String txnIdentifier, LocalDateTime completedTimestamp) {
        return new TransactionResponseData(txnIdentifier, routingCode, externalId, state, format(expiration), requestCode,
                format(completedTimestamp));
    }


    protected static String format(LocalDateTime date) {
        return date == null ? null : date.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
