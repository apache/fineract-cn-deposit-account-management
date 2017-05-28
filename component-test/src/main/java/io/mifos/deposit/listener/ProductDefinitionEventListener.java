/*
 * Copyright 2017 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.deposit.listener;

import io.mifos.core.lang.config.TenantHeaderFilter;
import io.mifos.core.test.listener.EventRecorder;
import io.mifos.deposit.AbstractDepositAccountManagementTest;
import io.mifos.deposit.api.v1.EventConstants;
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
}
