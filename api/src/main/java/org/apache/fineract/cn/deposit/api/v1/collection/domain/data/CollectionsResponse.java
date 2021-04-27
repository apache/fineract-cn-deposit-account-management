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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionsResponse {
    private String reference;
    private String token;
    @JsonFormat(pattern = "yyyy-MMMM-dd hh:mm:ss")
    private LocalDateTime tokenExpiresBy;
    private List<IndividualPayments> individualPayments;

    public CollectionsResponse() {
    }

    public CollectionsResponse(String reference, String token, LocalDateTime tokenExpiresBy, List<IndividualPayments> individualPayments) {
        this.reference = reference;
        this.token = token;
        this.tokenExpiresBy = tokenExpiresBy;
        this.individualPayments = individualPayments;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public List<IndividualPayments> getIndividualPayments() {
        return individualPayments;
    }

    public void setIndividualPayments(List<IndividualPayments> individualPayments) {
        this.individualPayments = individualPayments;
    }
}
