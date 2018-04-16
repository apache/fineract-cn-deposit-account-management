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

import javax.validation.constraints.NotNull;
import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;

public class Charge {

  @ValidIdentifier
  private String actionIdentifier;
  @ValidIdentifier
  private String incomeAccountIdentifier;
  @NotNull
  private String name;
  private String description;
  private Boolean proportional;
  private Double amount;

  public Charge() {
    super();
  }

  public String getActionIdentifier() {
    return this.actionIdentifier;
  }

  public void setActionIdentifier(final String actionIdentifier) {
    this.actionIdentifier = actionIdentifier;
  }

  public String getIncomeAccountIdentifier() {
    return this.incomeAccountIdentifier;
  }

  public void setIncomeAccountIdentifier(final String incomeAccountIdentifier) {
    this.incomeAccountIdentifier = incomeAccountIdentifier;
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

  public Boolean getProportional() {
    return this.proportional;
  }

  public void setProportional(final Boolean proportional) {
    this.proportional = proportional;
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setAmount(final Double amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Charge charge = (Charge) o;

    return name != null ? name.equals(charge.name) : charge.name == null;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
