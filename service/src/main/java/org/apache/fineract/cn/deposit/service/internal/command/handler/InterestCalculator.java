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
package org.apache.fineract.cn.deposit.service.internal.command.handler;

import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.fineract.cn.accounting.api.v1.domain.Account;
import org.apache.fineract.cn.accounting.api.v1.domain.AccountEntry;
import org.apache.fineract.cn.accounting.api.v1.domain.Creditor;
import org.apache.fineract.cn.accounting.api.v1.domain.Debtor;
import org.apache.fineract.cn.accounting.api.v1.domain.JournalEntry;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.CommandLogLevel;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.api.v1.domain.InterestPayable;
import org.apache.fineract.cn.deposit.api.v1.domain.Type;
import org.apache.fineract.cn.deposit.service.internal.command.AccrualCommand;
import org.apache.fineract.cn.deposit.service.internal.command.DividendDistributionCommand;
import org.apache.fineract.cn.deposit.service.internal.command.PayInterestCommand;
import org.apache.fineract.cn.deposit.service.internal.repository.AccruedInterestEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.AccruedInterestRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.DividendDistributionEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.DividendDistributionRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductInstanceEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductInstanceRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.TermEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.TermRepository;
import org.apache.fineract.cn.deposit.service.internal.service.helper.AccountingService;
import org.apache.fineract.cn.lang.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.threeten.extra.YearQuarter;

@Aggregate
public class InterestCalculator {
  private static final int INTEREST_PRECISION = 7;
  private static final String ACTIVE = "ACTIVE";
  private final ProductDefinitionRepository productDefinitionRepository;
  private final ProductInstanceRepository productInstanceRepository;
  private final TermRepository termRepository;
  private final AccountingService accountingService;
  private final AccruedInterestRepository accruedInterestRepository;
  private final DividendDistributionRepository dividendDistributionRepository;

  @Autowired
  public InterestCalculator(
      final ProductDefinitionRepository productDefinitionRepository,
      final ProductInstanceRepository productInstanceRepository,
      final TermRepository termRepository,
      final AccountingService accountingService,
      final AccruedInterestRepository accruedInterestRepository,
      final DividendDistributionRepository dividendDistributionRepository) {
    super();
    this.productDefinitionRepository = productDefinitionRepository;
    this.productInstanceRepository = productInstanceRepository;
    this.termRepository = termRepository;
    this.accruedInterestRepository = accruedInterestRepository;
    this.accountingService = accountingService;
    this.dividendDistributionRepository = dividendDistributionRepository;
  }

  @Transactional
  @CommandHandler(logStart = CommandLogLevel.DEBUG, logFinish =  CommandLogLevel.DEBUG)
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.INTEREST_ACCRUED)
  public String process(final AccrualCommand accrualCommand) {
    final LocalDate accrualDate = accrualCommand.dueDate();

    final List<ProductDefinitionEntity> productDefinitions = this.productDefinitionRepository.findAll();

    productDefinitions.forEach(productDefinitionEntity -> {
      if (this.accruableProduct(productDefinitionEntity)) {

        final ArrayList<Double> accruedValues = new ArrayList<>();

        final TermEntity term = this.termRepository.findByProductDefinition(productDefinitionEntity);

        final List<ProductInstanceEntity> productInstances =
            this.productInstanceRepository.findByProductDefinition(productDefinitionEntity);

        productInstances.forEach(productInstanceEntity -> {
          if (productInstanceEntity.getState().equals(ACTIVE)) {

            final Account account = this.accountingService.findAccount(productInstanceEntity.getAccountIdentifier());

            if (account.getBalance() > 0.00D) {
              final BigDecimal balance = BigDecimal.valueOf(account.getBalance());

              final BigDecimal rate = BigDecimal.valueOf(productDefinitionEntity.getInterest())
                  .divide(BigDecimal.valueOf(100), INTEREST_PRECISION, BigDecimal.ROUND_HALF_EVEN);

              final BigDecimal accruedInterest =
                  accruedInterest(balance, rate,
                      this.periodOfInterestPayable(term.getInterestPayable()), accrualDate.lengthOfYear());

              if (accruedInterest.compareTo(BigDecimal.ZERO) > 0) {
                final Double doubleValue =
                    accruedInterest.setScale(5, BigDecimal.ROUND_HALF_EVEN).doubleValue();

                accruedValues.add(doubleValue);

                final Optional<AccruedInterestEntity> optionalAccruedInterest =
                    this.accruedInterestRepository.findByCustomerAccountIdentifier(account.getIdentifier());
                if (optionalAccruedInterest.isPresent()) {
                  final AccruedInterestEntity accruedInterestEntity = optionalAccruedInterest.get();
                  accruedInterestEntity.setAmount(accruedInterestEntity.getAmount() + doubleValue);
                  this.accruedInterestRepository.save(accruedInterestEntity);
                } else {
                  final AccruedInterestEntity accruedInterestEntity = new AccruedInterestEntity();
                  accruedInterestEntity.setAccrueAccountIdentifier(productDefinitionEntity.getAccrueAccountIdentifier());
                  accruedInterestEntity.setCustomerAccountIdentifier(account.getIdentifier());
                  accruedInterestEntity.setAmount(doubleValue);
                  this.accruedInterestRepository.save(accruedInterestEntity);
                }
              }
            }
          }
        });

        final String roundedAmount =
            BigDecimal.valueOf(accruedValues.parallelStream().reduce(0.00D, Double::sum))
                .setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();

        final JournalEntry cashToAccrueJournalEntry = new JournalEntry();
        cashToAccrueJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32));
        cashToAccrueJournalEntry.setTransactionDate(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())));
        cashToAccrueJournalEntry.setTransactionType("INTR");
        cashToAccrueJournalEntry.setClerk(UserContextHolder.checkedGetUser());
        cashToAccrueJournalEntry.setNote("Daily accrual for product " + productDefinitionEntity.getIdentifier() + ".");

        final Debtor cashDebtor = new Debtor();
        cashDebtor.setAccountNumber(productDefinitionEntity.getCashAccountIdentifier());
        cashDebtor.setAmount(roundedAmount);
        cashToAccrueJournalEntry.setDebtors(Sets.newHashSet(cashDebtor));

        final Creditor accrueCreditor = new Creditor();
        accrueCreditor.setAccountNumber(productDefinitionEntity.getAccrueAccountIdentifier());
        accrueCreditor.setAmount(roundedAmount);
        cashToAccrueJournalEntry.setCreditors(Sets.newHashSet(accrueCreditor));

        this.accountingService.post(cashToAccrueJournalEntry);
      }
    });

    return DateConverter.toIsoString(accrualDate);
  }

  @Transactional
  @CommandHandler(logStart = CommandLogLevel.DEBUG, logFinish = CommandLogLevel.DEBUG)
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.INTEREST_PAYED)
  public String process(final PayInterestCommand payInterestCommand) {
    final List<ProductDefinitionEntity> productDefinitionEntities = this.productDefinitionRepository.findAll();

    productDefinitionEntities.forEach(productDefinitionEntity -> {
      if (productDefinitionEntity.getActive()
          && !productDefinitionEntity.getType().equals(Type.SHARE.name())) {
        final TermEntity term = this.termRepository.findByProductDefinition(productDefinitionEntity);
        if (this.shouldPayInterest(term.getInterestPayable(), payInterestCommand.date())) {
          final List<ProductInstanceEntity> productInstanceEntities =
              this.productInstanceRepository.findByProductDefinition(productDefinitionEntity);

          productInstanceEntities.forEach(productInstanceEntity -> {
            final Optional<AccruedInterestEntity> optionalAccruedInterestEntity =
                this.accruedInterestRepository.findByCustomerAccountIdentifier(productInstanceEntity.getAccountIdentifier());

            if (optionalAccruedInterestEntity.isPresent()) {
              final AccruedInterestEntity accruedInterestEntity = optionalAccruedInterestEntity.get();

              final String roundedAmount =
                  BigDecimal.valueOf(accruedInterestEntity.getAmount())
                      .setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();

              final JournalEntry accrueToExpenseJournalEntry = new JournalEntry();
              accrueToExpenseJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32));
              accrueToExpenseJournalEntry.setTransactionDate(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())));
              accrueToExpenseJournalEntry.setTransactionType("INTR");
              accrueToExpenseJournalEntry.setClerk(UserContextHolder.checkedGetUser());
              accrueToExpenseJournalEntry.setNote("Interest paid.");

              final Debtor accrueDebtor = new Debtor();
              accrueDebtor.setAccountNumber(accruedInterestEntity.getAccrueAccountIdentifier());
              accrueDebtor.setAmount(roundedAmount);
              accrueToExpenseJournalEntry.setDebtors(Sets.newHashSet(accrueDebtor));

              final Creditor expenseCreditor = new Creditor();
              expenseCreditor.setAccountNumber(productDefinitionEntity.getExpenseAccountIdentifier());
              expenseCreditor.setAmount(roundedAmount);
              accrueToExpenseJournalEntry.setCreditors(Sets.newHashSet(expenseCreditor));

              this.accruedInterestRepository.delete(accruedInterestEntity);

              this.accountingService.post(accrueToExpenseJournalEntry);

              this.payoutInterest(
                  productDefinitionEntity.getExpenseAccountIdentifier(),
                  accruedInterestEntity.getCustomerAccountIdentifier(),
                  roundedAmount
              );
            }
          });
        }
      }
    });

    return EventConstants.INTEREST_PAYED;
  }

  @Transactional
  @CommandHandler(logStart = CommandLogLevel.DEBUG, logFinish = CommandLogLevel.DEBUG)
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.DIVIDEND_DISTRIBUTION)
  public String process(final DividendDistributionCommand dividendDistributionCommand) {
    final Optional<ProductDefinitionEntity> optionalProductDefinition =
        this.productDefinitionRepository.findByIdentifier(dividendDistributionCommand.productDefinition());
    if (optionalProductDefinition.isPresent()) {
      final ProductDefinitionEntity productDefinitionEntity = optionalProductDefinition.get();
      if (productDefinitionEntity.getActive()) {
        final BigDecimal rate = BigDecimal.valueOf(dividendDistributionCommand.rate());
        final TermEntity term = this.termRepository.findByProductDefinition(productDefinitionEntity);
        final List<String> dateRanges = this.dateRanges(dividendDistributionCommand.dueDate(), term.getInterestPayable());

        final List<ProductInstanceEntity> productInstanceEntities =
            this.productInstanceRepository.findByProductDefinition(productDefinitionEntity);
        productInstanceEntities.forEach((ProductInstanceEntity productInstanceEntity) -> {
          if (productInstanceEntity.getState().equals(ACTIVE)) {

            final Account account =
                this.accountingService.findAccount(productInstanceEntity.getAccountIdentifier());

            final LocalDate startDate = dividendDistributionCommand.dueDate().plusDays(1);
            final LocalDate now = LocalDate.now(Clock.systemUTC());

            final String findCurrentEntries = DateConverter.toIsoString(startDate) + ".." + DateConverter.toIsoString(now);
            final List<AccountEntry> currentAccountEntries =
                this.accountingService.fetchEntries(account.getIdentifier(), findCurrentEntries, Sort.Direction.ASC.name());

            final BalanceHolder balanceHolder;
            if (currentAccountEntries.isEmpty()) {
              balanceHolder = new BalanceHolder(BigDecimal.valueOf(account.getBalance()));
            } else {
              final AccountEntry accountEntry = currentAccountEntries.get(0);
              balanceHolder = new BalanceHolder(BigDecimal.valueOf(accountEntry.getBalance()).subtract(BigDecimal.valueOf(accountEntry.getAmount())));
            }

            final DividendHolder dividendHolder = new DividendHolder();
            dateRanges.forEach(dateRange -> {
              final List<AccountEntry> accountEntries =
                  this.accountingService.fetchEntries(account.getIdentifier(), dateRange, Sort.Direction.DESC.name());
              if (!accountEntries.isEmpty()) {
                balanceHolder.setBalance(BigDecimal.valueOf(accountEntries.get(0).getBalance()));
              }

              final BigDecimal currentBalance = balanceHolder.getBalance();
              dividendHolder.addAmount(
                  accruedInterest(currentBalance, rate, 12, dividendDistributionCommand.dueDate().lengthOfYear())
              );
            });

            if (dividendHolder.getAmount().compareTo(BigDecimal.ZERO) > 0) {

              final String roundedAmount =
                  dividendHolder.getAmount()
                      .setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();

              final JournalEntry cashToExpenseJournalEntry = new JournalEntry();
              cashToExpenseJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32));
              cashToExpenseJournalEntry.setTransactionDate(DateConverter.toIsoString(now));
              cashToExpenseJournalEntry.setTransactionType("INTR");
              cashToExpenseJournalEntry.setClerk(UserContextHolder.checkedGetUser());
              cashToExpenseJournalEntry.setNote("Dividend distribution.");

              final Debtor cashDebtor = new Debtor();
              cashDebtor.setAccountNumber(productDefinitionEntity.getCashAccountIdentifier());
              cashDebtor.setAmount(roundedAmount);
              cashToExpenseJournalEntry.setDebtors(Sets.newHashSet(cashDebtor));

              final Creditor expenseCreditor = new Creditor();
              expenseCreditor.setAccountNumber(productDefinitionEntity.getExpenseAccountIdentifier());
              expenseCreditor.setAmount(roundedAmount);
              cashToExpenseJournalEntry.setCreditors(Sets.newHashSet(expenseCreditor));

              this.accountingService.post(cashToExpenseJournalEntry);

              this.payoutInterest(
                  productDefinitionEntity.getExpenseAccountIdentifier(),
                  account.getIdentifier(),
                  roundedAmount
              );
            }
          }
        });
      }
      final DividendDistributionEntity dividendDistributionEntity = new DividendDistributionEntity();

      dividendDistributionEntity.setProductDefinition(productDefinitionEntity);
      dividendDistributionEntity.setDueDate(Date.valueOf(dividendDistributionCommand.dueDate()));
      dividendDistributionEntity.setRate(dividendDistributionCommand.rate());
      dividendDistributionEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
      dividendDistributionEntity.setCreatedBy(UserContextHolder.checkedGetUser());

      this.dividendDistributionRepository.save(dividendDistributionEntity);
    }

    return dividendDistributionCommand.productDefinition();
  }

  private int periodOfInterestPayable(final String interestPayable) {
    switch (InterestPayable.valueOf(interestPayable)) {
      case MONTHLY:
        return 12;
      case QUARTERLY:
        return 4;
      default:
        return 1;
    }
  }

  private boolean shouldPayInterest(final String interestPayable, final LocalDate date) {
    switch (InterestPayable.valueOf(interestPayable)) {
      case MONTHLY:
        return date.equals(date.withDayOfMonth(date.lengthOfMonth()));
      case QUARTERLY:
        return date.equals(YearQuarter.from(date).atEndOfQuarter());
      case ANNUALLY:
        return date.getDayOfYear() == date.lengthOfYear();
      default:
        return false;
    }
  }

  private List<String> dateRanges(final LocalDate dueDate, final String interestPayable) {
    final int pastDays;
    switch (InterestPayable.valueOf(interestPayable)) {
      case MONTHLY:
        pastDays = dueDate.lengthOfMonth();
        break;
      case QUARTERLY:
        pastDays = YearQuarter.from(dueDate).lengthOfQuarter();
        break;
      default:
        pastDays = dueDate.lengthOfYear();
    }

    return IntStream
        .range(1, pastDays)
        .mapToObj(value -> {
          final LocalDate before = dueDate.minusDays(value);
          return DateConverter.toIsoString(before) + ".." + DateConverter.toIsoString(dueDate.minusDays(value - 1));
        }).collect(Collectors.toList());
  }

  private class BalanceHolder {
    private BigDecimal balance;

    private BalanceHolder(final BigDecimal balance) {
      super();
      this.balance = balance;
    }

    private BigDecimal getBalance() {
      return this.balance;
    }

    private void setBalance(final BigDecimal balance) {
      this.balance = balance;
    }
  }

  private class DividendHolder {
    private BigDecimal amount;

    private DividendHolder() {
      super();
      this.amount = BigDecimal.ZERO;
    }

    private void addAmount(final BigDecimal toAdd) {
      this.amount = this.amount.add(toAdd);
    }

    private BigDecimal getAmount() {
      return this.amount;
    }
  }

  private boolean accruableProduct(final ProductDefinitionEntity productDefinitionEntity) {
    return productDefinitionEntity.getActive()
        && !productDefinitionEntity.getType().equals(Type.SHARE.name())
        && productDefinitionEntity.getInterest() != null
        && productDefinitionEntity.getInterest() > 0.00D;
  }

  private void payoutInterest(final String expenseAccount, final String customerAccount, final String amount) {
    final JournalEntry expenseToCustomerJournalEntry = new JournalEntry();
    expenseToCustomerJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32));
    expenseToCustomerJournalEntry.setTransactionDate(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())));
    expenseToCustomerJournalEntry.setTransactionType("INTR");
    expenseToCustomerJournalEntry.setClerk(UserContextHolder.checkedGetUser());
    expenseToCustomerJournalEntry.setNote("Interest paid.");

    final Debtor expenseDebtor = new Debtor();
    expenseDebtor.setAccountNumber(expenseAccount);
    expenseDebtor.setAmount(amount);
    expenseToCustomerJournalEntry.setDebtors(Sets.newHashSet(expenseDebtor));

    final Creditor customerCreditor = new Creditor();
    customerCreditor.setAccountNumber(customerAccount);
    customerCreditor.setAmount(amount);
    expenseToCustomerJournalEntry.setCreditors(Sets.newHashSet(customerCreditor));

    this.accountingService.post(expenseToCustomerJournalEntry);

  }

  /**
   * Copied from JavaMoney AnnualPercentageYield.calculate, and adjusted.
   * @return the resulting amount, never null.
   */
  private static BigDecimal accruedInterest(
      final BigDecimal amount,
      final BigDecimal rate,
      final int periods,
      final int lengthOfYear) {
    final BigDecimal baseFactor = rate.divide(BigDecimal.valueOf(periods),MathContext.DECIMAL64)
        .add(BigDecimal.ONE);
    final BigDecimal annualInterest = amount.multiply(baseFactor.pow(periods).subtract(BigDecimal.ONE));

    return annualInterest
            .divide(BigDecimal.valueOf(lengthOfYear),
                amount.scale() + INTEREST_PRECISION, BigDecimal.ROUND_HALF_EVEN);
  }
}