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
import org.apache.fineract.cn.lang.DateOfBirth;
import org.hibernate.validator.constraints.NotBlank;

public class DividendDistribution {

  @NotNull
  private DateOfBirth dueDate;
  @NotBlank
  private String dividendRate;

  public DividendDistribution() {
    super();
  }

  public DateOfBirth getDueDate() {
    return this.dueDate;
  }

  public void setDueDate(final DateOfBirth dueDate) {
    this.dueDate = dueDate;
  }

  public String getDividendRate() {
    return this.dividendRate;
  }

  public void setDividendRate(final String dividendRate) {
    this.dividendRate = dividendRate;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final DividendDistribution that = (DividendDistribution) o;

    if (!dueDate.toLocalDate().isEqual(that.dueDate.toLocalDate())) return false;
    return dividendRate.equals(that.dividendRate);
  }

  @Override
  public int hashCode() {
    int result = dueDate.toLocalDate().hashCode();
    result = 31 * result + dividendRate.hashCode();
    return result;
  }
}
