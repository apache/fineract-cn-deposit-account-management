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

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.fineract.cn.postgresql.util.LocalDateConverter;
import org.apache.fineract.cn.postgresql.util.LocalDateTimeConverter;

@Entity
@Table(name = "shed_product_instances")
public class ProductInstanceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "customer_identifier", nullable = false)
  private String customerIdentifier;
  @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "product_definition_id", nullable = false)
  private ProductDefinitionEntity productDefinition;
  @Column(name = "account_identifier", nullable = false)
  private String accountIdentifier;
  @Column(name = "beneficiaries", nullable = true, length = 256)
  private String beneficiaries;
  @Convert(converter = LocalDateConverter.class)
  @Column(name = "opened_on", nullable = false)
  private LocalDate openedOn;
  @Convert(converter = LocalDateTimeConverter.class)
  @Column(name = "last_transaction_date", nullable = false)
  private LocalDateTime lastTransactionDate;
  @Column(name = "a_state", nullable = false)
  private String state;
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

  public ProductInstanceEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getCustomerIdentifier() {
    return this.customerIdentifier;
  }

  public void setCustomerIdentifier(final String customerIdentifier) {
    this.customerIdentifier = customerIdentifier;
  }

  public ProductDefinitionEntity getProductDefinition() {
    return this.productDefinition;
  }

  public void setProductDefinition(final ProductDefinitionEntity productDefinition) {
    this.productDefinition = productDefinition;
  }

  public String getAccountIdentifier() {
    return this.accountIdentifier;
  }

  public void setAccountIdentifier(final String accountIdentifier) {
    this.accountIdentifier = accountIdentifier;
  }

  public String getBeneficiaries() {
    return this.beneficiaries;
  }

  public void setBeneficiaries(final String beneficiaries) {
    this.beneficiaries = beneficiaries;
  }

  public LocalDate getOpenedOn() {
    return this.openedOn;
  }

  public void setOpenedOn(final LocalDate openedOn) {
    this.openedOn = openedOn;
  }

  public LocalDateTime getLastTransactionDate() {
    return this.lastTransactionDate;
  }

  public void setLastTransactionDate(final LocalDateTime lastTransactionDate) {
    this.lastTransactionDate = lastTransactionDate;
  }

  public String getState() {
    return this.state;
  }

  public void setState(final String state) {
    this.state = state;
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
