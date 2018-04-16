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
package org.apache.fineract.cn.deposit.api.v1.definition.domain;

import org.apache.fineract.cn.deposit.api.v1.domain.Type;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;

public class ProductDefinition {

  @Valid
  private Type type;
  @ValidIdentifier
  private String identifier;
  @NotNull
  private String name;
  private String description;
  @Valid
  @NotNull
  private Currency currency;
  @NotNull
  private Double minimumBalance;
  @ValidIdentifier(maxLength = 34)
  private String equityLedgerIdentifier;
  @ValidIdentifier(maxLength = 34)
  private String cashAccountIdentifier;
  @ValidIdentifier(maxLength = 34)
  private String expenseAccountIdentifier;
  @ValidIdentifier(maxLength = 34, optional = true)
  private String accrueAccountIdentifier;
  private Double interest;
  @Valid
  @NotNull
  private Term term;
  @Valid
  private Set<Charge> charges;
  @NotNull
  private Boolean flexible;
  private Boolean active;

  public ProductDefinition() {
    super();
  }

  public String getType() {
    return this.type.name();
  }

  public void setType(final String type) {
    this.type = Type.valueOf(type);
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(final String identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public Currency getCurrency() {
    return this.currency;
  }

  public void setCurrency(final Currency currency) {
    this.currency = currency;
  }

  public Double getMinimumBalance() {
    return this.minimumBalance;
  }

  public void setMinimumBalance(final Double minimumBalance) {
    this.minimumBalance = minimumBalance;
  }

  public String getEquityLedgerIdentifier() {
    return this.equityLedgerIdentifier;
  }

  public void setEquityLedgerIdentifier(final String equityLedgerIdentifier) {
    this.equityLedgerIdentifier = equityLedgerIdentifier;
  }

  public String getCashAccountIdentifier() {
    return this.cashAccountIdentifier;
  }

  public void setCashAccountIdentifier(final String cashAccountIdentifier) {
    this.cashAccountIdentifier = cashAccountIdentifier;
  }

  public String getExpenseAccountIdentifier() {
    return this.expenseAccountIdentifier;
  }

  public void setExpenseAccountIdentifier(final String expenseAccountIdentifier) {
    this.expenseAccountIdentifier = expenseAccountIdentifier;
  }

  public String getAccrueAccountIdentifier() {
    return this.accrueAccountIdentifier;
  }

  public void setAccrueAccountIdentifier(final String accrueAccountIdentifier) {
    this.accrueAccountIdentifier = accrueAccountIdentifier;
  }

  public Double getInterest() {
    return this.interest;
  }

  public void setInterest(final Double interest) {
    this.interest = interest;
  }

  public Term getTerm() {
    return this.term;
  }

  public void setTerm(final Term term) {
    this.term = term;
  }

  public Set<Charge> getCharges() {
    return this.charges;
  }

  public void setCharges(final Set<Charge> charges) {
    this.charges = charges;
  }

  public Boolean getFlexible() {
    return this.flexible;
  }

  public void setFlexible(final Boolean flexible) {
    this.flexible = flexible;
  }

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }
}
