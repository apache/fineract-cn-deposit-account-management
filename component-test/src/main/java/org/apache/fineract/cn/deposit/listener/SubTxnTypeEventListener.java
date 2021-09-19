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

package org.apache.fineract.cn.deposit.listener;

import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.lang.config.TenantHeaderFilter;
import org.apache.fineract.cn.test.listener.EventRecorder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;

/**
 * @author manoj
 */
public class SubTxnTypeEventListener {
    private final Logger logger;
    private final EventRecorder eventRecorder;

    @Autowired
    public SubTxnTypeEventListener(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                    final EventRecorder eventRecorder) {
        super();
        this.logger = logger;
        this.eventRecorder = eventRecorder;
    }

    @JmsListener(
            destination = EventConstants.DESTINATION,
            selector = EventConstants.SELECTOR_POST_SUB_TXN_TYPE,
            subscription = EventConstants.DESTINATION
    )
    public void onCreateSubTxnType(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                          final String payload) {
        this.logger.debug("Product definition created.");
        this.eventRecorder.event(tenant, EventConstants.POST_SUB_TXN_TYPE, payload, String.class);
    }


    @JmsListener(
            destination = EventConstants.DESTINATION,
            selector = EventConstants.SELECTOR_PUT_SUB_TXN_TYPE,
            subscription = EventConstants.DESTINATION
    )
    public void onUpdateSubTxnType(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                          final String payload) {
        this.logger.debug("Product definition created.");
        this.eventRecorder.event(tenant, EventConstants.PUT_SUB_TXN_TYPE, payload, String.class);
    }


}
