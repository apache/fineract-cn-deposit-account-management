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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "shed_terms")
public class TermEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @OneToOne
  @JoinColumn(name = "product_definition_id", nullable = false, unique = true)
  private ProductDefinitionEntity productDefinition;
  @Column(name = "period", nullable = false)
  private Integer period;
  @Column(name = "time_unit", nullable = false)
  private String timeUnit;
  @Column(name = "interest_payable", nullable = false)
  private String interestPayable;

  public TermEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public ProductDefinitionEntity getProductDefinition() {
    return this.productDefinition;
  }

  public void setProductDefinition(final ProductDefinitionEntity productDefinition) {
    this.productDefinition = productDefinition;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Integer getPeriod() {
    return this.period;
  }

  public void setPeriod(final Integer period) {
    this.period = period;
  }

  public String getTimeUnit() {
    return this.timeUnit;
  }

  public void setTimeUnit(final String timeUnit) {
    this.timeUnit = timeUnit;
  }

  public String getInterestPayable() {
    return this.interestPayable;
  }

  public void setInterestPayable(final String interestPayable) {
    this.interestPayable = interestPayable;
  }
}
