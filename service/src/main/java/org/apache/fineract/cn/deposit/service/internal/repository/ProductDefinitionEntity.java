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
package org.apache.fineract.cn.deposit.service.internal.repository;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.fineract.cn.postgresql.util.LocalDateTimeConverter;

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
  @Column(name = "minimum_balance", nullable = true)
  private Double minimumBalance;
  @Column(name = "equity_ledger_identifier", nullable = false)
  private String equityLedgerIdentifier;
  @Column(name = "cash_account_identifier", nullable = false)
  private String cashAccountIdentifier;
  @Column(name = "expense_account_identifier", nullable = false)
  private String expenseAccountIdentifier;
  @Column(name = "accrue_account_identifier", nullable = true)
  private String accrueAccountIdentifier;
  @Column(name = "interest", nullable = true)
  private Double interest;
  @Column(name = "is_flexible", nullable = false)
  private Boolean flexible;
  @Column(name = "is_active", nullable = false)
  private Boolean active;
  @Column(name = "created_by", nullable = false, length = 32)
  private String createdBy;
  @Convert(converter = LocalDateTimeConverter.class)
  @Column(name = "created_on", nullable = false)
  private LocalDateTime createdOn;
  @Column(name = "last_modified_by", nullable = false, length = 32)
  private String lastModifiedBy;
  @Convert(converter = LocalDateTimeConverter.class)
  @Column(name = "last_modified_on", nullable = false)
  private LocalDateTime lastModifiedOn;

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

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getCreatedOn() {
    return this.createdOn;
  }

  public void setCreatedOn(final LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(final String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public LocalDateTime getLastModifiedOn() {
    return this.lastModifiedOn;
  }

  public void setLastModifiedOn(final LocalDateTime lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }
}
