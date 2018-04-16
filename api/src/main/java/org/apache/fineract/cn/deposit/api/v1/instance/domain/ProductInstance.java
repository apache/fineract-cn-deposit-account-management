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
package org.apache.fineract.cn.deposit.api.v1.instance.domain;

import java.util.Set;
import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;

public class ProductInstance {

  @ValidIdentifier
  private String customerIdentifier;
  @ValidIdentifier
  private String productIdentifier;
  @ValidIdentifier(maxLength = 34, optional = true)
  private String accountIdentifier;
  private String alternativeAccountNumber;
  private Set<String> beneficiaries;
  private String openedOn;
  private String lastTransactionDate;
  private String state;
  private Double balance;

  public ProductInstance() {
    super();
  }

  public String getCustomerIdentifier() {
    return this.customerIdentifier;
  }

  public void setCustomerIdentifier(final String customerIdentifier) {
    this.customerIdentifier = customerIdentifier;
  }

  public String getProductIdentifier() {
    return this.productIdentifier;
  }

  public void setProductIdentifier(final String productIdentifier) {
    this.productIdentifier = productIdentifier;
  }

  public String getAccountIdentifier() {
    return this.accountIdentifier;
  }

  public void setAccountIdentifier(final String accountIdentifier) {
    this.accountIdentifier = accountIdentifier;
  }

  public String getAlternativeAccountNumber() {
    return alternativeAccountNumber;
  }

  public void setAlternativeAccountNumber(String alternativeAccountNumber) {
    this.alternativeAccountNumber = alternativeAccountNumber;
  }

  public Set<String> getBeneficiaries() {
    return this.beneficiaries;
  }

  public void setBeneficiaries(final Set<String> beneficiaries) {
    this.beneficiaries = beneficiaries;
  }

  public String getOpenedOn() {
    return this.openedOn;
  }

  public void setOpenedOn(final String openedOn) {
    this.openedOn = openedOn;
  }

  public String getLastTransactionDate() {
    return this.lastTransactionDate;
  }

  public void setLastTransactionDate(final String lastTransactionDate) {
    this.lastTransactionDate = lastTransactionDate;
  }

  public String getState() {
    return this.state;
  }

  public void setState(final String state) {
    this.state = state;
  }

  public Double getBalance() {
    return this.balance;
  }

  public void setBalance(final Double balance) {
    this.balance = balance;
  }
}
