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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shed_accrued_interests")
public class AccruedInterestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "accrue_account_identifier", nullable = false)
  private String accrueAccountIdentifier;
  @Column(name = "customer_account_identifier", nullable = false)
  private String customerAccountIdentifier;
  @Column(name = "amount", nullable = false)
  private Double amount;

  public AccruedInterestEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getAccrueAccountIdentifier() {
    return this.accrueAccountIdentifier;
  }

  public void setAccrueAccountIdentifier(final String accrueAccountIdentifier) {
    this.accrueAccountIdentifier = accrueAccountIdentifier;
  }

  public String getCustomerAccountIdentifier() {
    return this.customerAccountIdentifier;
  }

  public void setCustomerAccountIdentifier(final String customerAccountIdentifier) {
    this.customerAccountIdentifier = customerAccountIdentifier;
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setAmount(final Double amount) {
    this.amount = amount;
  }
}
