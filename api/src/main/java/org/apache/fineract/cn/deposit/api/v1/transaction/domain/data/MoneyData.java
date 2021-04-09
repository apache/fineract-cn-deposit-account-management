/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.deposit.api.v1.transaction.domain.data;

import org.apache.fineract.cn.deposit.api.v1.definition.domain.Currency;
import org.apache.fineract.cn.deposit.api.v1.transaction.utils.MathUtil;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MoneyData {

    @NotNull
    @Min(0)
    @Digits(integer = 15, fraction = 4) // interoperation schema allows integer = 18, AccountEntity amount allows fraction = 5
    private BigDecimal amount;

    @NotNull
    @Length(min = 3, max = 3)
    private String currency;

    public MoneyData() {
    }

    public MoneyData(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static MoneyData build(BigDecimal amount, String currency) {
        return amount == null ? null : new MoneyData(amount, currency);
    }

    public static MoneyData build(BigDecimal amount, Currency currency) {
        return amount == null ? null : build(amount, currency.getCode());
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void normalizeAmount(@NotNull Currency currency) {
        if (!currency.getCode().equals(this.currency))
            throw new UnsupportedOperationException("Internal error: Invalid currency " + currency.getCode());
        MathUtil.normalize(amount, currency);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
