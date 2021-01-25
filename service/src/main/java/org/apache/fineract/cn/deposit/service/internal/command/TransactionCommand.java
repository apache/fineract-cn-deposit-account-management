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
