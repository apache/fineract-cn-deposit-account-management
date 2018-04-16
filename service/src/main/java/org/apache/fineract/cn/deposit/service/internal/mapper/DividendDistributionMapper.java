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
package org.apache.fineract.cn.deposit.service.internal.mapper;

import org.apache.fineract.cn.deposit.api.v1.definition.domain.DividendDistribution;
import org.apache.fineract.cn.deposit.service.internal.repository.DividendDistributionEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionEntity;
import java.sql.Date;
import java.time.Clock;
import java.time.LocalDateTime;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.lang.DateOfBirth;

public class DividendDistributionMapper {

  private DividendDistributionMapper() {
    super();
  }

  public static DividendDistributionEntity map(final DividendDistribution dividendDistribution,
                                               final ProductDefinitionEntity productDefinitionEntity) {
    final DividendDistributionEntity dividendDistributionEntity = new DividendDistributionEntity();

    dividendDistributionEntity.setProductDefinition(productDefinitionEntity);
    final Date dueDate = Date.valueOf(dividendDistribution.getDueDate().toLocalDate());
    dividendDistributionEntity.setDueDate(dueDate);
    dividendDistributionEntity.setRate(Double.valueOf(dividendDistribution.getDividendRate()));
    dividendDistributionEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
    dividendDistributionEntity.setCreatedBy(UserContextHolder.checkedGetUser());

    return dividendDistributionEntity;
  }

  public static DividendDistribution map(final DividendDistributionEntity dividendDistributionEntity) {
    final DividendDistribution dividendDistribution = new DividendDistribution();

    dividendDistribution.setDividendRate(dividendDistributionEntity.getRate().toString());
    dividendDistribution.setDueDate(DateOfBirth
        .fromLocalDate(dividendDistributionEntity.getDueDate().toLocalDate()));

    return dividendDistribution;
  }
}
