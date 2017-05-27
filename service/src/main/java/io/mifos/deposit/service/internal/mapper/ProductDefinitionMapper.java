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
package io.mifos.deposit.service.internal.mapper;

import io.mifos.deposit.api.v1.definition.domain.ProductDefinition;
import io.mifos.deposit.service.internal.repository.ActionRepository;
import io.mifos.deposit.service.internal.repository.ProductDefinitionEntity;

import java.util.stream.Collectors;

public class ProductDefinitionMapper {

  private ProductDefinitionMapper() {
    super();
  }

  public static ProductDefinitionEntity map(final ProductDefinition productDefinition, final ActionRepository actionRepository) {
    final ProductDefinitionEntity productDefinitionEntity = new ProductDefinitionEntity();
    productDefinitionEntity.setType(productDefinition.getType());
    productDefinitionEntity.setIdentifier(productDefinition.getIdentifier());
    productDefinitionEntity.setName(productDefinition.getName());
    productDefinitionEntity.setDescription(productDefinition.getName());
    productDefinitionEntity.setCurrency(CurrencyMapper.map(productDefinition.getCurrency()));
    productDefinitionEntity.setMinimumBalance(productDefinition.getMinimumBalance());
    productDefinitionEntity.setEquityLedgerIdentifier(productDefinition.getEquityLedgerIdentifier());
    productDefinitionEntity.setExpenseAccountIdentifier(productDefinition.getExpenseAccountIdentifier());
    productDefinitionEntity.setInterest(productDefinition.getInterest());
    productDefinitionEntity.setTerm(TermMapper.map(productDefinition.getTerm()));
    productDefinitionEntity.setCharges(productDefinition.getCharges()
        .stream()
        .map(charge -> ChargesMapper.map(charge, actionRepository))
        .collect(Collectors.toList())
    );
    productDefinitionEntity.setFlexible(productDefinition.getFlexible());

    return productDefinitionEntity;
  }

  public static ProductDefinition map (final ProductDefinitionEntity productDefinitionEntity) {
    final ProductDefinition productDefinition = new ProductDefinition();
    productDefinition.setType(productDefinitionEntity.getType());
    productDefinition.setIdentifier(productDefinitionEntity.getIdentifier());
    productDefinition.setName(productDefinitionEntity.getName());
    productDefinition.setDescription(productDefinitionEntity.getName());
    productDefinition.setCurrency(CurrencyMapper.map(productDefinitionEntity.getCurrency()));
    productDefinition.setMinimumBalance(productDefinitionEntity.getMinimumBalance());
    productDefinition.setEquityLedgerIdentifier(productDefinitionEntity.getEquityLedgerIdentifier());
    productDefinition.setExpenseAccountIdentifier(productDefinitionEntity.getExpenseAccountIdentifier());
    productDefinition.setInterest(productDefinitionEntity.getInterest());
    productDefinition.setTerm(TermMapper.map(productDefinitionEntity.getTerm()));
    productDefinition.setCharges(productDefinitionEntity.getCharges()
        .stream()
        .map(ChargesMapper::map)
        .collect(Collectors.toList())
    );
    productDefinition.setFlexible(productDefinitionEntity.getFlexible());

    return productDefinition;
  }
}
