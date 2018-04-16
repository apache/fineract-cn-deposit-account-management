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
package org.apache.fineract.cn.deposit.service.internal.command;

import java.time.LocalDate;

public class DividendDistributionCommand {

  private final String productDefinition;
  private final LocalDate dueDate;
  private final Double rate;

  public DividendDistributionCommand(final String productDefinition, final LocalDate dueDate, final Double rate) {
    super();
    this.productDefinition = productDefinition;
    this.dueDate = dueDate;
    this.rate = rate;
  }

  public String productDefinition() {
    return this.productDefinition;
  }

  public LocalDate dueDate() {
    return this.dueDate;
  }

  public Double rate() {
    return this.rate;
  }
}
