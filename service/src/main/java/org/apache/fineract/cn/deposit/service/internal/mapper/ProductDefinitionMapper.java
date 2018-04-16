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

import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionEntity;

public class ProductDefinitionMapper {

  private ProductDefinitionMapper() {
    super();
  }

  public static ProductDefinitionEntity map(final ProductDefinition productDefinition) {
    final ProductDefinitionEntity productDefinitionEntity = new ProductDefinitionEntity();
    productDefinitionEntity.setType(productDefinition.getType());
    productDefinitionEntity.setIdentifier(productDefinition.getIdentifier());
    productDefinitionEntity.setName(productDefinition.getName());
    productDefinitionEntity.setDescription(productDefinition.getName());
    productDefinitionEntity.setMinimumBalance(productDefinition.getMinimumBalance());
    productDefinitionEntity.setEquityLedgerIdentifier(productDefinition.getEquityLedgerIdentifier());
    productDefinitionEntity.setCashAccountIdentifier(productDefinition.getCashAccountIdentifier());
    productDefinitionEntity.setExpenseAccountIdentifier(productDefinition.getExpenseAccountIdentifier());
    productDefinitionEntity.setAccrueAccountIdentifier(productDefinition.getAccrueAccountIdentifier());
    productDefinitionEntity.setInterest(productDefinition.getInterest());
    productDefinitionEntity.setFlexible(productDefinition.getFlexible());

    return productDefinitionEntity;
  }

  public static ProductDefinition map (final ProductDefinitionEntity productDefinitionEntity) {
    final ProductDefinition productDefinition = new ProductDefinition();
    productDefinition.setType(productDefinitionEntity.getType());
    productDefinition.setIdentifier(productDefinitionEntity.getIdentifier());
    productDefinition.setName(productDefinitionEntity.getName());
    productDefinition.setDescription(productDefinitionEntity.getName());
    productDefinition.setMinimumBalance(productDefinitionEntity.getMinimumBalance());
    productDefinition.setEquityLedgerIdentifier(productDefinitionEntity.getEquityLedgerIdentifier());
    productDefinition.setCashAccountIdentifier(productDefinitionEntity.getCashAccountIdentifier());
    productDefinition.setExpenseAccountIdentifier(productDefinitionEntity.getExpenseAccountIdentifier());
    productDefinition.setAccrueAccountIdentifier(productDefinitionEntity.getAccrueAccountIdentifier());
    productDefinition.setInterest(productDefinitionEntity.getInterest());
    productDefinition.setFlexible(productDefinitionEntity.getFlexible());
    productDefinition.setActive(productDefinitionEntity.getActive());

    return productDefinition;
  }
}
