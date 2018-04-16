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

import org.apache.fineract.cn.deposit.api.v1.definition.domain.Currency;
import org.apache.fineract.cn.deposit.service.internal.repository.CurrencyEntity;

public class CurrencyMapper {

  private CurrencyMapper() {
    super();
  }

  public static CurrencyEntity map(final Currency currency) {
    final CurrencyEntity currencyEntity = new CurrencyEntity();
    currencyEntity.setCode(currency.getCode());
    currencyEntity.setName(currency.getName());
    currencyEntity.setSign(currency.getSign());
    currencyEntity.setScale(currency.getScale());

    return currencyEntity;
  }

  public static Currency map(final CurrencyEntity currencyEntity) {
    final Currency currency = new Currency();
    currency.setCode(currencyEntity.getCode());
    currency.setName(currencyEntity.getName());
    currency.setSign(currencyEntity.getSign());
    currency.setScale(currencyEntity.getScale());

    return currency;
  }
}
