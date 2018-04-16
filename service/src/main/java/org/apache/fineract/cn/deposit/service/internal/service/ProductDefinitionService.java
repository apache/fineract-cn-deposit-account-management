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
package org.apache.fineract.cn.deposit.service.internal.service;

import org.apache.fineract.cn.deposit.api.v1.definition.domain.DividendDistribution;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.mapper.ChargeMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.CurrencyMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.DividendDistributionMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.ProductDefinitionCommandMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.ProductDefinitionMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.TermMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.ActionRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ChargeRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.CurrencyRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.DividendDistributionRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionCommandRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionRepository;
import org.apache.fineract.cn.deposit.service.internal.repository.TermRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProductDefinitionService {

  private final Logger logger;
  private final ProductDefinitionRepository productDefinitionRepository;
  private final ProductDefinitionCommandRepository productDefinitionCommandRepository;
  private final ActionRepository actionRepository;
  private final ChargeRepository chargeRepository;
  private final CurrencyRepository currencyRepository;
  private final TermRepository termRepository;
  private final DividendDistributionRepository dividendDistributionRepository;

  @Autowired
  public ProductDefinitionService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                  final ProductDefinitionRepository productDefinitionRepository,
                                  final ProductDefinitionCommandRepository productDefinitionCommandRepository,
                                  final ActionRepository actionRepository,
                                  final ChargeRepository chargeRepository,
                                  final CurrencyRepository currencyRepository,
                                  final TermRepository termRepository,
                                  final DividendDistributionRepository dividendDistributionRepository) {
    super();
    this.logger = logger;
    this.productDefinitionRepository = productDefinitionRepository;
    this.productDefinitionCommandRepository = productDefinitionCommandRepository;
    this.actionRepository = actionRepository;
    this.chargeRepository = chargeRepository;
    this.currencyRepository = currencyRepository;
    this.termRepository = termRepository;
    this.dividendDistributionRepository = dividendDistributionRepository;
  }

  public List<ProductDefinition> fetchProductDefinitions() {
    return this.productDefinitionRepository.findAll()
        .stream()
        .map(this::getProductDefinition)
        .collect(Collectors.toList());
  }

  public Optional<ProductDefinition> findProductDefinition(final String identifier) {
    return this.productDefinitionRepository.findByIdentifier(identifier)
        .map(this::getProductDefinition);
  }

  private ProductDefinition getProductDefinition(final ProductDefinitionEntity productDefinitionEntity) {
    final ProductDefinition productDefinition = ProductDefinitionMapper.map(productDefinitionEntity);
    productDefinition.setCurrency(
        CurrencyMapper.map(this.currencyRepository.findByProductDefinition(productDefinitionEntity))
    );
    productDefinition.setTerm(
        TermMapper.map(this.termRepository.findByProductDefinition(productDefinitionEntity))
    );
    productDefinition.setCharges(
        this.chargeRepository.findByProductDefinition(productDefinitionEntity)
            .stream()
            .map(chargeEntity -> ChargeMapper.map(chargeEntity, this.actionRepository))
            .collect(Collectors.toSet())
    );
    return productDefinition;
  }

  public List<ProductDefinitionCommand> findCommands(final String identifier) {
    return this.productDefinitionRepository.findByIdentifier(identifier)
        .map(productDefinitionEntity -> this.productDefinitionCommandRepository.findByProductDefinition(productDefinitionEntity)
            .stream()
            .map(ProductDefinitionCommandMapper::map)
            .collect(Collectors.toList()))
        .orElseGet(Collections::emptyList);
  }

  public List<DividendDistribution> fetchDividendDistributions(final String identifier) {
    final Optional<ProductDefinitionEntity> optionalProductDefinition =
        this.productDefinitionRepository.findByIdentifier(identifier);
    if (optionalProductDefinition.isPresent()) {
      return this.dividendDistributionRepository.findByProductDefinitionOrderByDueDateAsc(optionalProductDefinition.get())
          .stream().map(DividendDistributionMapper::map)
          .collect(Collectors.toList());
    } else {
      throw ServiceException.notFound("Product definition {0} not found", identifier);
    }
  }
}
