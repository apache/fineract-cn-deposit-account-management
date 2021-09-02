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

package org.apache.fineract.cn.deposit.service.internal.service;

import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.TokenStatus;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.repository.SelfExpiringTokenEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.SelfExpiringTokenRepository;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SelfExpiringTokenService {
    private final SelfExpiringTokenRepository selfExpiringTokenRepository;
    private final Logger logger;

    @Value("${config.otpTokenLength}")
    private Integer otpTokenLength;

    @Value("${config.tokenExpiryInSeconds}")
    private Integer tokenExpiryInSeconds;

    @Autowired
    public SelfExpiringTokenService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                    SelfExpiringTokenRepository selfExpiringTokenRepository) {
        this.selfExpiringTokenRepository = selfExpiringTokenRepository;
        this.logger = logger;
    }

    public SelfExpiringTokenEntity generateAndSaveToken(String entityType, String entityReference, LocalDateTime currentTime){
        SelfExpiringTokenEntity entity = new SelfExpiringTokenEntity();
        entity.setToken(generateUniqueToken());

        LocalDateTime tokenExpiresBy = currentTime.plusSeconds(tokenExpiryInSeconds);
        entity.setCreatedTime(currentTime);
        entity.setTokenExpiresBy(tokenExpiresBy);
        entity.setStatus(TokenStatus.ACTIVE.name());
        entity.setEntityType(entityType);
        entity.setEntityReference(entityReference);
        this.selfExpiringTokenRepository.save(entity);
        return entity;
    }

    public SelfExpiringTokenEntity fetchActiveToken(String token){
        return this.selfExpiringTokenRepository.findByTokenAndStatus(token, TokenStatus.ACTIVE.name()).orElseThrow(
                ()-> ServiceException.notFound("Active token not found")
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markTokenAsUsed(SelfExpiringTokenEntity selfExpiringTokenEntity){
        selfExpiringTokenEntity.setStatus(TokenStatus.USED.name());
        this.selfExpiringTokenRepository.save(selfExpiringTokenEntity);
    }

    private String generateUniqueToken(){
        String token = generateRandomToken(otpTokenLength);
        while(this.selfExpiringTokenRepository.findByTokenAndStatus(token, TokenStatus.ACTIVE.name()).isPresent()){
            token = generateRandomToken(otpTokenLength);
        }
        return token;
    }
    private String generateRandomToken(Integer length){
        final char[] buf = new char[length];
        final String alphanum = "0123456789" ;
        final char[] symbols = alphanum.toCharArray();
        final Random random= new SecureRandom();
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}
