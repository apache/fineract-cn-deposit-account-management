/*
 * Copyright 2017 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.deposit.service.internal.repository;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "shed_product_definitions")
public class ProductDefinitionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "a_type", nullable = false)
  private String type;
  @Column(name = "identifier", nullable = false, unique = true, length = 32)
  private String identifier;
  @Column(name = "a_name", nullable = false, length = 256)
  private String name;
  @Column(name = "description", nullable = true, length = 4096)
  private String description;
  @OneToOne(mappedBy = "productDefinition", cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
  private CurrencyEntity currency;
  @Column(name = "minimum_balance", nullable = true)
  private Double minimumBalance;
  @Column(name = "equity_ledger_identifier", nullable = false)
  private String equityLedgerIdentifier;
  @Column(name = "expense_account_identifier", nullable = false)
  private String expenseAccountIdentifier;
  @Column(name = "interest", nullable = true)
  private Double interest;
  @OneToOne(mappedBy = "productDefinition", cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
  private TermEntity term;
  @OneToMany(mappedBy = "productDefinition", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<ChargeEntity> charges;
  @Column(name = "is_flexible", nullable = false)
  private Boolean flexible;
  @Column(name = "is_active", nullable = false)
  private Boolean active;

  public ProductDefinitionEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
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

  public CurrencyEntity getCurrency() {
    return this.currency;
  }

  public void setCurrency(final CurrencyEntity currency) {
    this.currency = currency;
    this.currency.setProductDefinition(this);
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

  public String getExpenseAccountIdentifier() {
    return this.expenseAccountIdentifier;
  }

  public void setExpenseAccountIdentifier(final String expenseAccountIdentifier) {
    this.expenseAccountIdentifier = expenseAccountIdentifier;
  }

  public Double getInterest() {
    return this.interest;
  }

  public void setInterest(final Double interest) {
    this.interest = interest;
  }

  public TermEntity getTerm() {
    return this.term;
  }

  public void setTerm(final TermEntity term) {
    this.term = term;
    this.term.setProductDefinition(this);
  }

  public List<ChargeEntity> getCharges() {
    return this.charges;
  }

  public void setCharges(final List<ChargeEntity> charges) {
    this.charges = charges;
    charges.forEach(chargeEntity -> chargeEntity.setProductDefinition(this));
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
