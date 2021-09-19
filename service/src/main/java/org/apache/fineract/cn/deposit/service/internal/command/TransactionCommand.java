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
package org.apache.fineract.cn.deposit.service.internal.command;

import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.TransactionActionType;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.TransactionRequestData;

public class TransactionCommand {
    private final TransactionRequestData transactionRequest;
    private final TransactionActionType action;

    public TransactionCommand(TransactionRequestData transactionRequest, TransactionActionType action) {
        this.transactionRequest = transactionRequest;
        this.action = action;
    }

    public TransactionRequestData getTransactionRequest() {
        return transactionRequest;
    }

    public TransactionActionType getAction() {
        return action;
    }
}
