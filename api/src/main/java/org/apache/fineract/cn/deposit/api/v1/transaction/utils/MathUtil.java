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
package org.apache.fineract.cn.deposit.api.v1.transaction.utils;

import org.apache.fineract.cn.deposit.api.v1.definition.domain.Currency;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtil {

    public static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(2, RoundingMode.HALF_EVEN);
    public static final MathContext CALCULATION_MATH_CONTEXT = new MathContext(5, RoundingMode.HALF_EVEN);

    public static Double nullToZero(Double value) {
        return nullToDefault(value, 0D);
    }

    public static Double nullToDefault(Double value, Double def) {
        return value == null ? def : value;
    }

    public static Double zeroToNull(Double value) {
        return isEmpty(value) ? null : value;
    }

    /** @return parameter value or ZERO if it is negative */
    public static Double negativeToZero(Double value) {
        return isGreaterThanZero(value) ? value : 0D;
    }

    public static boolean isEmpty(Double value) {
        return value == null || value.equals(0D);
    }

    public static boolean isGreaterThanZero(Double value) {
        return value != null && value > 0D;
    }

    public static boolean isLessThanZero(Double value) {
        return value != null && value < 0D;
    }

    public static boolean isZero(Double value) {
        return value != null && value.equals(0D);
    }

    public static boolean isEqualTo(Double first, Double second) {
        return nullToZero(first).equals(nullToZero(second));
    }

    public static boolean isGreaterThan(Double first, Double second) {
        return nullToZero(first) > nullToZero(second);
    }

    public static boolean isLessThan(Double first, Double second) {
        return nullToZero(first) < nullToZero(second);
    }

    public static boolean isGreaterThanOrEqualTo(Double first, Double second) {
        return nullToZero(first) >= nullToZero(second);
    }

    public static boolean isLessThanOrEqualZero(Double value) {
        return nullToZero(value) <= 0D;
    }

    /** @return parameter value or negated value to positive */
    public static Double abs(Double value) {
        return value == null ? 0D : Math.abs(value);
    }

    /** @return calculates minimum of the two values considering null values
     * @param notNull if true then null parameter is omitted, otherwise returns null */
    public static Double min(Double first, Double second, boolean notNull) {
        return first == null
                ? (notNull ? second : null)
                : second == null ? (notNull ? first : null) : Math.min(first, second);
    }

    /** @return calculates minimum of the values considering null values
     * @param notNull if true then null parameter is omitted, otherwise returns null */
    public static Double min(Double first, Double second, Double third, boolean notNull) {
        return min(min(first, second, notNull), third, notNull);
    }

    /** @return sum the two values considering null values */
    public static Double add(Double first, Double second) {
        return first == null
                ? second
                : second == null ? first : first + second;
    }

    /** @return sum the values considering null values */
    public static Double add(Double first, Double second, Double third) {
        return add(add(first, second), third);
    }

    /** @return sum the values considering null values */
    public static Double add(Double first, Double second, Double third, Double fourth) {
        return add(add(add(first, second), third), fourth);
    }

    /** @return sum the values considering null values */
    public static Double add(Double first, Double second, Double third, Double fourth, Double fifth) {
        return add(add(add(add(first, second), third), fourth), fifth);
    }

    /** @return first minus second considering null values, maybe negative */
    public static Double subtract(Double first, Double second) {
        return first == null
                ? null
                : second == null ? first : first - second;
    }

    /** @return first minus the others considering null values, maybe negative */
    public static Double subtractToZero(Double first, Double second, Double third) {
        return subtractToZero(subtract(first, second), third);
    }

    /** @return first minus the others considering null values, maybe negative */
    public static Double subtractToZero(Double first, Double second, Double third, Double fourth) {
        return subtractToZero(subtract(subtract(first, second), third), fourth);
    }

    /** @return NONE negative first minus second considering null values */
    public static Double subtractToZero(Double first, Double second) {
        return negativeToZero(subtract(first, second));
    }

    /** @return BigDecimal with scale set to the 'digitsAfterDecimal' of the parameter currency */
    public static Double normalize(Double amount, @NotNull Currency currency) {
        return amount == null ? null : normalize(BigDecimal.valueOf(amount), currency).doubleValue();
    }

    /** @return BigDecimal with scale set to the 'digitsAfterDecimal' of the parameter currency */
    public static Double normalize(Double amount, @NotNull MathContext mc) {
        return amount == null ? null : normalize(BigDecimal.valueOf(amount), mc).doubleValue();
    }

    /** @return BigDecimal null safe negate */
    public static Double negate(Double amount) {
        return isEmpty(amount) ? amount : amount * -1;
    }


    // ----------------- BigDecimal -----------------

    public static BigDecimal nullToZero(BigDecimal value) {
        return nullToDefault(value, BigDecimal.ZERO);
    }

    public static BigDecimal nullToDefault(BigDecimal value, BigDecimal def) {
        return value == null ? def : value;
    }

    public static BigDecimal zeroToNull(BigDecimal value) {
        return isEmpty(value) ? null : value;
    }

    /** @return parameter value or ZERO if it is negative */
    public static BigDecimal negativeToZero(BigDecimal value) {
        return isGreaterThanZero(value) ? value : BigDecimal.ZERO;
    }

    public static boolean isEmpty(BigDecimal value) {
        return value == null || BigDecimal.ZERO.compareTo(value) == 0;
    }

    public static boolean isGreaterThanZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isLessThanZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isEqualTo(BigDecimal first, BigDecimal second) {
        return nullToZero(first).compareTo(nullToZero(second)) == 0;
    }

    public static boolean isGreaterThan(BigDecimal first, BigDecimal second) {
        return nullToZero(first).compareTo(nullToZero(second)) > 0;
    }

    public static boolean isLessThan(BigDecimal first, BigDecimal second) {
        return nullToZero(first).compareTo(nullToZero(second)) < 0;
    }

    public static boolean isGreaterThanOrEqualTo(BigDecimal first, BigDecimal second) {
        return nullToZero(first).compareTo(nullToZero(second)) >= 0;
    }

    public static boolean isLessThanOrEqualZero(BigDecimal value) {
        return nullToZero(value).compareTo(BigDecimal.ZERO) <= 0;
    }

    /** @return parameter value or negated value to positive */
    public static BigDecimal abs(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.abs();
    }

    /** @return calculates minimum of the two values considering null values
     * @param notNull if true then null parameter is omitted, otherwise returns null */
    public static BigDecimal min(BigDecimal first, BigDecimal second, boolean notNull) {
        return notNull
                ? first == null
                ? second
                : second == null ? first : min(first, second, false)
                : isLessThan(first, second) ? first : second;
    }

    /** @return calculates minimum of the values considering null values
     * @param notNull if true then null parameter is omitted, otherwise returns null */
    public static BigDecimal min(BigDecimal first, BigDecimal second, BigDecimal third, boolean notNull) {
        return min(min(first, second, notNull), third, notNull);
    }

    /** @return sum the two values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second) {
        return add(first, second, CALCULATION_MATH_CONTEXT);
    }

    /** @return sum the two values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, MathContext mc) {
        return first == null
                ? second
                : second == null ? first : first.add(second, mc);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third) {
        return add(first, second, third, CALCULATION_MATH_CONTEXT);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third, MathContext mc) {
        return add(add(first, second, mc), third, mc);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth) {
        return add(first, second, third, fourth, CALCULATION_MATH_CONTEXT);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth, MathContext mc) {
        return add(add(add(first, second, mc), third, mc), fourth, mc);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth, BigDecimal fifth) {
        return add(first, second, third, fourth, fifth, CALCULATION_MATH_CONTEXT);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth, BigDecimal fifth, MathContext mc) {
        return add(add(add(add(first, second, mc), third, mc), fourth, mc), fifth, mc);
    }

    /** @return first minus second considering null values, maybe negative */
    public static BigDecimal subtract(BigDecimal first, BigDecimal second) {
        return first == null
                ? null
                : second == null ? first : first.subtract(second, CALCULATION_MATH_CONTEXT);
    }

    /** @return NONE negative first minus second considering null values */
    public static BigDecimal subtractToZero(BigDecimal first, BigDecimal second) {
        return negativeToZero(subtract(first, second));
    }

    /** @return first minus the others considering null values, maybe negative */
    public static BigDecimal subtractToZero(BigDecimal first, BigDecimal second, BigDecimal third) {
        MathContext mc = CALCULATION_MATH_CONTEXT;
        return subtractToZero(subtract(first, second), third);
    }

    /** @return first minus the others considering null values, maybe negative */
    public static BigDecimal subtractToZero(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth) {
        return subtractToZero(subtract(subtract(first, second), third), fourth);
    }

    /** @return BigDecimal with scale set to the 'digitsAfterDecimal' of the parameter currency */
    public static BigDecimal normalize(BigDecimal amount, @NotNull Currency currency) {
        return amount == null ? null : amount.setScale(currency.getScale(), CALCULATION_MATH_CONTEXT.getRoundingMode());
    }

    /** @return BigDecimal with scale set to the 'digitsAfterDecimal' of the parameter currency */
    public static BigDecimal normalize(BigDecimal amount, @NotNull MathContext mc) {
        return amount == null ? null : amount.setScale(mc.getPrecision(), mc.getRoundingMode());
    }

    /** @return BigDecimal null safe negate */
    public static BigDecimal negate(BigDecimal amount) {
        return negate(amount, CALCULATION_MATH_CONTEXT);
    }

    /** @return BigDecimal null safe negate */
    public static BigDecimal negate(BigDecimal amount, MathContext mc) {
        return isEmpty(amount) ? amount : amount.negate(mc);
    }
}
