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
package org.apache.fineract.cn.deposit;

import org.apache.fineract.cn.deposit.api.v1.definition.domain.Action;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Charge;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Currency;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Term;
import org.apache.fineract.cn.deposit.api.v1.domain.InterestPayable;
import org.apache.fineract.cn.deposit.api.v1.domain.TimeUnit;
import org.apache.fineract.cn.deposit.api.v1.domain.Type;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.HashSet;

public class Fixture {

  public Fixture() {
    super();
  }

  static ProductDefinition productDefinition() {
    final Charge openingCharge = new Charge();
    openingCharge.setActionIdentifier("Open");
    openingCharge.setAmount(2.00D);
    openingCharge.setName("Opening Account Charge");
    openingCharge.setIncomeAccountIdentifier("10123");
    openingCharge.setProportional(Boolean.FALSE);

    final Charge closingCharge = new Charge();
    closingCharge.setActionIdentifier("Close");
    closingCharge.setAmount(2.00D);
    closingCharge.setName("Closing Account Fee");
    closingCharge.setIncomeAccountIdentifier("10123");
    closingCharge.setProportional(Boolean.FALSE);

    final Term term = new Term();
    term.setPeriod(12);
    term.setTimeUnit(TimeUnit.MONTH.name());
    term.setInterestPayable(InterestPayable.MATURITY.name());

    final Currency currency = new Currency();
    currency.setCode("USD");
    currency.setName("US Dollar");
    currency.setSign("$");
    currency.setScale(3);

    final ProductDefinition productDefinition = new ProductDefinition();
    productDefinition.setType(Type.SAVINGS.name());
    productDefinition.setIdentifier("pdtDef" + RandomStringUtils.randomAlphanumeric(4));
    productDefinition.setName("PdtName " + RandomStringUtils.randomAlphanumeric(6));
    productDefinition.setDescription("Product Description" + RandomStringUtils.randomAlphanumeric(8));
    productDefinition.setCharges(new HashSet<>(Arrays.asList(openingCharge, closingCharge)));
    productDefinition.setCurrency(currency);
    productDefinition.setInterest(1.25D);
    productDefinition.setEquityLedgerIdentifier("91xx");
    productDefinition.setCashAccountIdentifier("76xx");
    productDefinition.setExpenseAccountIdentifier("38xx");
    productDefinition.setAccrueAccountIdentifier("82xx");
    productDefinition.setFlexible(Boolean.FALSE);
    productDefinition.setMinimumBalance(50.00);
    productDefinition.setTerm(term);

    return productDefinition;
  }

  public static Action action() {
    final Action action = new Action();
    action.setIdentifier("Acc" + RandomStringUtils.randomAlphanumeric(2));
    action.setName("Action" + RandomStringUtils.randomAlphanumeric(3));
    action.setDescription("Action's Description " + RandomStringUtils.randomAlphanumeric(4));
    action.setTransactionType("ACCO");

    return action;
  }

  public static ProductInstance productInstance(final String productIdentifier) {
    final ProductInstance productInstance = new ProductInstance();
    productInstance.setProductIdentifier(productIdentifier);
    productInstance.setCustomerIdentifier("Customer" + RandomStringUtils.randomAlphanumeric(3));
    productInstance.setBeneficiaries(new HashSet<>(Arrays.asList(
        "Benefiary" + RandomStringUtils.randomAlphanumeric(3),
        "Benefiary" + RandomStringUtils.randomAlphanumeric(3)
    )));
    return productInstance;
  }
}
