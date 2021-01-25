package org.apache.fineract.cn.deposit.service.internal.service;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.cn.accounting.api.v1.client.LedgerManager;
import org.apache.fineract.cn.accounting.api.v1.domain.*;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Action;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Charge;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Currency;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.SubTransactionType;
import org.apache.fineract.cn.deposit.api.v1.transaction.domain.data.*;
import org.apache.fineract.cn.deposit.api.v1.transaction.utils.MathUtil;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.TransactionCommand;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductInstanceEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductInstanceRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.TransactionEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.TransactionRepository;
import org.apache.fineract.cn.lang.DateConverter;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final Logger logger;
    private final LedgerManager ledgerManager;
    private final ProductInstanceService productInstanceService;
    private final ProductDefinitionService productDefinitionService;
    private final ActionService actionService;
    private final SubTxnTypesService subTxnTypesService;
    private final TransactionRepository transactionRepository;
    private final ProductInstanceRepository productInstanceRepository;

    @Autowired
    public TransactionService(@Qualifier(ServiceConstants.LOGGER_NAME) Logger logger, LedgerManager ledgerManager,
                              ProductInstanceService productInstanceService,
                              ProductDefinitionService productDefinitionService, ActionService actionService,
                              SubTxnTypesService subTxnTypesService, TransactionRepository transactionRepository, ProductInstanceRepository productInstanceRepository) {
        this.logger = logger;
        this.ledgerManager = ledgerManager;
        this.productInstanceService = productInstanceService;
        this.productDefinitionService = productDefinitionService;
        this.actionService = actionService;
        this.subTxnTypesService = subTxnTypesService;
        this.transactionRepository = transactionRepository;
        this.productInstanceRepository = productInstanceRepository;
    }

    @Transactional
    public TransactionResponseData withdraw(TransactionCommand command) {
        TransactionRequestData request = command.getTransactionRequest();
        AccountWrapper accountWrapper = validateAndGetAccount(request, TransactionTypeEnum.WITHDRAWAL);
        LocalDateTime transactionDate = getNow();
        //get txntype charges
        List<Charge> charges = getCharges(accountWrapper.account.getIdentifier(), TransactionTypeEnum.WITHDRAWAL);
        //todo: get subTxnType charges

        TransactionEntity txn = doWithdraw(request, accountWrapper, charges, getNow());


        return TransactionResponseData.build(request.getRoutingCode(), request.getExternalId(),
                request.getRequestCode(), ActionState.ACCEPTED,
                null, txn.getIdentifier(), transactionDate);
    }

    @Transactional
    public TransactionResponseData deposit(TransactionCommand command) {
        TransactionRequestData request = command.getTransactionRequest();
        AccountWrapper accountWrapper = validateAndGetAccount(request, TransactionTypeEnum.DEPOSIT);
        LocalDateTime transactionDate = getNow();
        //get txntype charges
        List<Charge> charges = getCharges(accountWrapper.account.getIdentifier(), TransactionTypeEnum.DEPOSIT);
        //todo: get subTxnType charges
        TransactionEntity txn = doDeposit(request, accountWrapper, charges, getNow());

        return TransactionResponseData.build(request.getRoutingCode(), request.getExternalId(),
                request.getRequestCode(), ActionState.ACCEPTED,
                null, txn.getIdentifier(), transactionDate);
    }

    private TransactionEntity doDeposit(TransactionRequestData request, AccountWrapper accountWrapper, List<Charge> charges, LocalDateTime transactionDate) {
        BigDecimal amount = request.getAmount().getAmount();

        TransactionEntity txn = createTransaction(request,TransactionTypeEnum.DEPOSIT, transactionDate);
        String debitAccountIdentifier = accountWrapper.productDefinition.getCashAccountIdentifier();
        /* if subtxn is provided and it has an account configured the do debit that account*/
        if(StringUtils.isNotBlank(request.getSubTxnId())){
            Optional<SubTransactionType> subTxnTypeOpt = this.subTxnTypesService.findByIdentifier(request.getSubTxnId());
            if(subTxnTypeOpt.isPresent()) {
                txn.setSubTxnType(subTxnTypeOpt.get().getIdentifier());
                if (subTxnTypeOpt.get().getLedgerAccount() != null) {
                    debitAccountIdentifier = subTxnTypeOpt.get().getLedgerAccount();
                }
            }
        }
        final JournalEntry journalEntry = createJournalEntry(txn.getIdentifier(), TransactionTypeEnum.DEPOSIT.getCode(),
                DateConverter.toIsoString(transactionDate), request.getNote(), getLoginUser());

        HashSet<Debtor> debtors = new HashSet<>(1);
        HashSet<Creditor> creditors = new HashSet<>(1);

        addCreditor(accountWrapper.account.getIdentifier(), amount.doubleValue(), creditors);
        addDebtor(debitAccountIdentifier, amount.doubleValue(), debtors);


        prepareCharges(request, accountWrapper, charges, debtors, creditors, txn);

        if (debtors.isEmpty()) // must be same size as creditors
            throw ServiceException.badRequest("Debit and Credit doesn't match");

        journalEntry.setDebtors(debtors);
        journalEntry.setCreditors(creditors);

        transactionRepository.save(txn);

        ledgerManager.createJournalEntry(journalEntry);
        return txn;
    }


    private TransactionEntity doWithdraw(@NotNull TransactionRequestData request, @NotNull AccountWrapper accountWrapper,
                                 List<Charge> charges, LocalDateTime transactionDate) {
        BigDecimal amount = request.getAmount().getAmount();

        TransactionEntity txn = createTransaction(request, TransactionTypeEnum.WITHDRAWAL, transactionDate);

        String creditAccountIdentifier = accountWrapper.productDefinition.getCashAccountIdentifier();
        /* if subtxn is provided and it has an account configured the do credit that account*/
        if(StringUtils.isNotBlank(request.getSubTxnId())){
            Optional<SubTransactionType> subTxnTypeOpt = this.subTxnTypesService.findByIdentifier(request.getSubTxnId());
            if(subTxnTypeOpt.isPresent()) {
                txn.setSubTxnType(subTxnTypeOpt.get().getIdentifier());
                if (subTxnTypeOpt.get().getLedgerAccount() != null) {
                    creditAccountIdentifier = subTxnTypeOpt.get().getLedgerAccount();
                }
            }
        }
        final JournalEntry journalEntry = createJournalEntry(txn.getIdentifier(), TransactionTypeEnum.WITHDRAWAL.getCode(),
                DateConverter.toIsoString(transactionDate), request.getNote(), getLoginUser());

        HashSet<Debtor> debtors = new HashSet<>(1);
        HashSet<Creditor> creditors = new HashSet<>(1);

        addCreditor(creditAccountIdentifier, amount.doubleValue(), creditors);
        addDebtor(accountWrapper.account.getIdentifier(), amount.doubleValue(), debtors);

        prepareCharges(request, accountWrapper, charges, debtors, creditors, txn);

        if (debtors.isEmpty()) // must be same size as creditors
            throw ServiceException.badRequest("Debit and Credit doesn't match");

        journalEntry.setDebtors(debtors);
        journalEntry.setCreditors(creditors);
        transactionRepository.save(txn);
        ledgerManager.createJournalEntry(journalEntry);
        return txn;
    }



    private void prepareCharges(@NotNull TransactionRequestData request, @NotNull AccountWrapper accountWrapper,
                                @NotNull List<Charge> charges, HashSet<Debtor> debtors, HashSet<Creditor> creditors,
                                TransactionEntity txn) {


        BigDecimal amount = request.getAmount().getAmount();
        Currency currency = accountWrapper.productDefinition.getCurrency();

        BigDecimal total = MathUtil.normalize(calcTotalCharges(charges, amount), currency);
        if (MathUtil.isEmpty(total)) {
            return;
        }
        txn.setFeeAmount(total);

        if (creditors == null) {
            creditors = new HashSet<>(1);
        }
        if (debtors == null) {
            debtors = new HashSet<>(charges.size());
        }
        for(Charge charge : charges){
            addCreditor(charge.getIncomeAccountIdentifier(), calcChargeAmount(amount, charge).doubleValue(), creditors);
            addDebtor(accountWrapper.account.getIdentifier(), calcChargeAmount(amount, charge).doubleValue(), debtors);
        }

    }



    private AccountWrapper validateAndGetAccount(@NotNull TransactionRequestData request, TransactionTypeEnum txnType) {
        //TODO: error handling
        String accountId = request.getAccountId();
        Account account = ledgerManager.findAccount(accountId);
        validateAccount(request, account);

        ProductInstance product = productInstanceService.findByAccountIdentifier(accountId).get();
        ProductDefinition productDefinition = productDefinitionService.findProductDefinition(product.getProductIdentifier()).get();

        Currency currency = productDefinition.getCurrency();
        if (!currency.getCode().equals(request.getAmount().getCurrency()))
            throw new UnsupportedOperationException();

        request.normalizeAmounts(currency);

        Double withdrawableBalance = getWithdrawableBalance(account, productDefinition);
        if (txnType == TransactionTypeEnum.WITHDRAWAL && withdrawableBalance < request.getAmount().getAmount().doubleValue())
            throw new UnsupportedOperationException();

        return new AccountWrapper(account, product, productDefinition, withdrawableBalance);
    }

    Double getWithdrawableBalance(Account account, ProductDefinition productDefinition) {
        // on-hold amount, if any, is subtracted to payable account
        return MathUtil.subtractToZero(account.getBalance(), productDefinition.getMinimumBalance());
    }


    private void validateAccount(@NotNull TransactionRequestData request, Account account) {
        validateAccount(account);

        String accountId = account.getIdentifier();

        if (account.getHolders() != null) { // customer account
            ProductInstance product = productInstanceService.findByAccountIdentifier(accountId).get();
            ProductDefinition productDefinition = productDefinitionService.findProductDefinition(product.getProductIdentifier()).get();
            if (!Boolean.TRUE.equals(productDefinition.getActive()))
                throw new UnsupportedOperationException("Product Definition is inactive");

            Currency currency = productDefinition.getCurrency();
            if (!currency.getCode().equals(request.getAmount().getCurrency()))
                throw new UnsupportedOperationException();
        }
    }

    private void validateAccount(Account account) {
        if (account == null)
            throw new UnsupportedOperationException("Account not found");
        if (!account.getState().equals(Account.State.OPEN.name()))
            throw new UnsupportedOperationException("Account is in state " + account.getState());
    }

    public List<Charge> getCharges(String accountIdentifier, TransactionTypeEnum transactionType) {
        List<Action> actions = actionService.fetchActions();

        List<String> actionIds = actions
                .stream()
                .filter(action -> action.getTransactionType().equals(transactionType.getCode()))
                .map(Action::getIdentifier)
                .collect(Collectors.toList());

        ProductInstance product = productInstanceService.findByAccountIdentifier(accountIdentifier).get();
        ProductDefinition productDefinition = productDefinitionService.findProductDefinition(product.getProductIdentifier()).get();

        return productDefinition.getCharges()
                .stream()
                .filter(charge -> actionIds.contains(charge.getActionIdentifier()))
                .collect(Collectors.toList());
    }
    // Util


    private LocalDateTime getNow() {
        return LocalDateTime.now(Clock.systemUTC());
    }
    private String getLoginUser() {
        return UserContextHolder.checkedGetUser();
    }


    private JournalEntry createJournalEntry(String actionIdentifier, String transactionType, String transactionDate, String message, String loginUser) {
        final JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTransactionIdentifier(actionIdentifier);
        journalEntry.setTransactionType(transactionType);
        journalEntry.setTransactionDate(transactionDate);
        journalEntry.setMessage(message);
        journalEntry.setClerk(loginUser);
        return journalEntry;
    }

    private void addCreditor(String accountNumber, double amount, HashSet<Creditor> creditors) {
        Creditor creditor = new Creditor();
        creditor.setAccountNumber(accountNumber);
        creditor.setAmount(Double.toString(amount));
        creditors.add(creditor);
    }

    private void addDebtor(String accountNumber, double amount, HashSet<Debtor> debtors) {
        Debtor debtor = new Debtor();
        debtor.setAccountNumber(accountNumber);
        debtor.setAmount(Double.toString(amount));
        debtors.add(debtor);
    }

    private BigDecimal calcChargeAmount(BigDecimal amount, Charge charge) {
        return calcChargeAmount(amount, charge, null, false);
    }

    private BigDecimal calcChargeAmount(@NotNull BigDecimal amount, @NotNull Charge charge, Currency currency, boolean norm) {
        Double value = charge.getAmount();
        if (value == null)
            return null;

        BigDecimal portion = BigDecimal.valueOf(100.00d);
        MathContext mc = MathUtil.CALCULATION_MATH_CONTEXT;
        BigDecimal feeAmount = BigDecimal.valueOf(MathUtil.nullToZero(charge.getAmount()));
        BigDecimal result = charge.getProportional()
                ? amount.multiply(feeAmount.divide(portion, mc), mc)
                : feeAmount;
        return norm ? MathUtil.normalize(result, currency) : result;
    }

    @NotNull
    private BigDecimal calcTotalCharges(@NotNull List<Charge> charges, BigDecimal amount) {
        return charges.stream().map(charge -> calcChargeAmount(amount, charge)).reduce(MathUtil::add).orElse(BigDecimal.ZERO);
    }

    private TransactionEntity createTransaction(TransactionRequestData request, TransactionTypeEnum txnType, LocalDateTime transactionDate) {
        TransactionEntity txn = new TransactionEntity();
        UUID uuid=UUID.randomUUID();

        txn.setIdentifier(uuid.toString());
        txn.setRoutingCode(request.getRoutingCode());
        txn.setExternalId(request.getExternalId());
        txn.setTransactionType(txnType);
        txn.setAmount(request.getAmount().getAmount());
        //txn.setFeeAmount();
        txn.setState(ActionState.ACCEPTED);
        //txn.setCustomerAccountIdentifier();
        txn.setTransactionDate(transactionDate);
        txn.setExpirationDate(request.getExpiration());
        txn.setCreatedBy(getLoginUser());
        txn.setCreatedOn(getNow());
        /*txn.setLastModifiedBy();
        txn.setLastModifiedOn();*/
        markLastTransaction(request.getAccountId(), transactionDate);
        txn.setAccountId(request.getAccountId());
        transactionRepository.save(txn);
        return txn;
    }

    private void markLastTransaction(final String productInstanceIdentifier, LocalDateTime transactionDate) {
        final Optional<ProductInstanceEntity> optionalProductInstance =
                this.productInstanceRepository.findByAccountIdentifier(productInstanceIdentifier);

        final ProductInstanceEntity productInstanceEntity = optionalProductInstance.orElseThrow(() ->
                ServiceException.notFound("Product instance {0} not found.", productInstanceIdentifier));

        productInstanceEntity.setLastTransactionDate(transactionDate);

        this.productInstanceRepository.save(productInstanceEntity);

    }

    public static class AccountWrapper {
        @NotNull
        private final Account account;
        @NotNull
        private final ProductInstance product;
        @NotNull
        private final ProductDefinition productDefinition;
        @NotNull
        private final Double withdrawableBalance;

        public AccountWrapper(Account account, ProductInstance product, ProductDefinition productDefinition, Double withdrawableBalance) {
            this.account = account;
            this.product = product;
            this.productDefinition = productDefinition;
            this.withdrawableBalance = withdrawableBalance;
        }
    }
}
