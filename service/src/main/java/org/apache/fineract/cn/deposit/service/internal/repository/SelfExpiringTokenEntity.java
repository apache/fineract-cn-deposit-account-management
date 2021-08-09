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

package org.apache.fineract.cn.deposit.service.internal.repository;

import org.apache.fineract.cn.postgresql.util.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shed_self_expiring_tokens")
public class SelfExpiringTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", length = 10)
    private String token;

    @Column(name = "created_time")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdTime;

    @Column(name = "token_expires_by")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime tokenExpiresBy;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "entity_type", length = 36)
    private String entityType;

    @Column(name = "entity_reference", length = 36)
    private String entityReference;

    public SelfExpiringTokenEntity() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getTokenExpiresBy() {
        return tokenExpiresBy;
    }

    public void setTokenExpiresBy(LocalDateTime tokenExpiresBy) {
        this.tokenExpiresBy = tokenExpiresBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(String entityReference) {
        this.entityReference = entityReference;
    }
}
