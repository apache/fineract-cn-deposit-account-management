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

import org.apache.fineract.cn.deposit.api.v1.collection.domain.data.CollectionsResponse;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.mapper.CollectionsMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.CollectionsRepository;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CollectionsService {
    private final Logger logger;
    private final CollectionsRepository collectionsRepository;

    @Autowired
    public CollectionsService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                              CollectionsRepository collectionsRepository) {
        this.collectionsRepository = collectionsRepository;
        this.logger = logger;
    }

    public CollectionsResponse fetchCollection(String collectionReference){
        return CollectionsMapper.map(this.collectionsRepository.findByCollectionReference(collectionReference)
                .orElseThrow(() -> ServiceException.notFound("collection {0} not found ", collectionReference)));
    }
}
