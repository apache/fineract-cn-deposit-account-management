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
package io.mifos.deposit.api.v1.definition.domain;

import io.mifos.core.lang.validation.constraints.ValidIdentifier;
import io.mifos.deposit.api.v1.domain.Type;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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
  private Double interest;
  @Valid
  @NotNull
  private Term term;
  @Valid
  private List<Charge> charges;
  @Valid
  private List<Action> actions;
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

  public List<Charge> getCharges() {
    return this.charges;
  }

  public void setCharges(final List<Charge> charges) {
    this.charges = charges;
  }

  public List<Action> getActions() {
    return this.actions;
  }

  public void setActions(final List<Action> actions) {
    this.actions = actions;
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
