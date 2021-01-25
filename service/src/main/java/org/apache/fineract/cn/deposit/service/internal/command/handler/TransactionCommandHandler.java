package org.apache.fineract.cn.deposit.service.internal.command.handler;

import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.CommandLogLevel;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.TransactionResponseData;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.TransactionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.TransactionProcessedCommand;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductInstanceEntity;
import org.apache.fineract.cn.deposit.service.internal.service.TransactionService;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Aggregate
public class TransactionCommandHandler {
    private final Logger logger;
    private final TransactionService transactionService;

    @Autowired
    public TransactionCommandHandler(@Qualifier(ServiceConstants.LOGGER_NAME)Logger logger,
                                     TransactionService transactionService) {
        this.logger = logger;
        this.transactionService = transactionService;
    }

    @NotNull
    @Transactional
    @CommandHandler(logStart = CommandLogLevel.INFO, logFinish = CommandLogLevel.INFO)
    public TransactionResponseData performTransfer(@NotNull TransactionCommand command) {
        switch (command.getAction()) {
            case WITHDRAWAL: {
                //command = dataValidator.validatePrepareTransfer(command);
                return transactionService.withdraw(command);
            }
            case DEPOSIT: {
                //command = dataValidator.validateCommitTransfer(command);
                return transactionService.deposit(command);
            }
            default:
                return null;
        }
    }

}
