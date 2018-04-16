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
package org.apache.fineract.cn.deposit.listener;

import org.apache.fineract.cn.deposit.AbstractDepositAccountManagementTest;
import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.lang.config.TenantHeaderFilter;
import org.apache.fineract.cn.test.listener.EventRecorder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ProductDefinitionEventListener {

  private final Logger logger;
  private final EventRecorder eventRecorder;

  @Autowired
  public ProductDefinitionEventListener(@Qualifier(AbstractDepositAccountManagementTest.TEST_LOGGER) final Logger logger,
                                        final EventRecorder eventRecorder) {
    super();
    this.logger = logger;
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_POST_PRODUCT_DEFINITION,
      subscription = EventConstants.DESTINATION
  )
  public void onCreateProductDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                        final String payload) {
    this.logger.debug("Product definition created.");
    this.eventRecorder.event(tenant, EventConstants.POST_PRODUCT_DEFINITION, payload, String.class);
  }

  @JmsListener(
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_POST_PRODUCT_COMMAND,
      subscription = EventConstants.DESTINATION
  )
  public void onActivateProductDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                          final String payload) {
    this.logger.debug("Product definition activated.");
    this.eventRecorder.event(tenant, EventConstants.POST_PRODUCT_DEFINITION_COMMAND, payload, String.class);
  }

  @JmsListener(
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_DELETE_PRODUCT_DEFINITION,
      subscription = EventConstants.DESTINATION
  )
  public void onDeleteProductDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                          final String payload) {
    this.logger.debug("Product definition activated.");
    this.eventRecorder.event(tenant, EventConstants.DELETE_PRODUCT_DEFINITION, payload, String.class);
  }

  @JmsListener(
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_PUT_PRODUCT_DEFINITION,
      subscription = EventConstants.DESTINATION
  )
  public void onChangeProductDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                        final String payload) {
    this.logger.debug("Product definition activated.");
    this.eventRecorder.event(tenant, EventConstants.PUT_PRODUCT_DEFINITION, payload, String.class);
  }
}
