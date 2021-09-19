/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fineract.cn.deposit.api.v1.collection.domain.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndividualPayments{
    @NotNull
    private String accountNumber;
    private BigDecimal amount;
    private AttendanceEnum attendance;
    private String reference;
    private String token;
    @JsonFormat(pattern = "yyyy-MMMM-dd hh:mm:ss")
    private LocalDateTime tokenExpiresBy;

    public IndividualPayments() {
    }

    public IndividualPayments(String accountNumber, BigDecimal amount, AttendanceEnum attendance, String reference) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.attendance = attendance;
        this.reference = reference;
    }

    public IndividualPayments(String accountNumber, AttendanceEnum attendance, String reference,
                              BigDecimal amount, String token, LocalDateTime tokenExpiresBy){
        this.accountNumber = accountNumber;
        this.attendance = attendance;
        this.reference =reference;
        this.token = token;
        this.tokenExpiresBy = tokenExpiresBy;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAttendance() {
        return attendance==null? null: attendance.name();
    }

    public void setAttendance(String attendance) {
        this.attendance = AttendanceEnum.valueOf(attendance);
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setAttendance(AttendanceEnum attendance) {
        this.attendance = attendance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getTokenExpiresBy() {
        return tokenExpiresBy;
    }

    public void setTokenExpiresBy(LocalDateTime tokenExpiresBy) {
        this.tokenExpiresBy = tokenExpiresBy;
    }
}