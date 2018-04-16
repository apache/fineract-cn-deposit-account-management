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

import org.apache.fineract.cn.deposit.api.v1.domain.InterestPayable;
import org.apache.fineract.cn.deposit.api.v1.domain.TimeUnit;

import javax.validation.constraints.NotNull;

public class Term {

  private Integer period;
  private TimeUnit timeUnit;
  @NotNull
  private InterestPayable interestPayable;

  public Term() {
    super();
  }

  public Integer getPeriod() {
    return this.period;
  }

  public void setPeriod(final Integer period) {
    this.period = period;
  }

  public String getTimeUnit() {
    if (this.timeUnit != null) {
      return this.timeUnit.name();
    } else {
      return null;
    }
  }

  public void setTimeUnit(final String timeUnit) {
    if (timeUnit != null) {
      this.timeUnit = TimeUnit.valueOf(timeUnit);
    }
  }

  public String getInterestPayable() {
    return this.interestPayable.name();
  }

  public void setInterestPayable(final String interestPayable) {
    this.interestPayable = InterestPayable.valueOf(interestPayable);
  }
}
