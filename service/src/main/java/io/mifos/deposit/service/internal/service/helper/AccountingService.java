package io.mifos.deposit.service.internal.service.helper;

import io.mifos.accounting.api.v1.client.LedgerManager;
import io.mifos.accounting.api.v1.domain.Account;
import io.mifos.deposit.service.ServiceConstants;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AccountingService {

  private final Logger logger;
  private final LedgerManager ledgerManager;

  @Autowired
  public AccountingService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                           final LedgerManager ledgerManager) {
    super();
    this.logger = logger;
    this.ledgerManager = ledgerManager;
  }

  public void createAccount(final String ledgerIdentifier, final String accountIdentifier, final String productName) {
    final Account account = new Account();
    account.setIdentifier(accountIdentifier);
    account.setLedger(ledgerIdentifier);
    account.setName(productName);
    account.setBalance(0.00D);
    this.ledgerManager.createAccount(account);
  }
}
