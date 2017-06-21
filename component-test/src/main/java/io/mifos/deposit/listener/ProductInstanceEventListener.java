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
import io.mifos.deposit.api.v1.EventConstants;
import io.mifos.deposit.service.ServiceConstants;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ProductInstanceEventListener {

  private final Logger logger;
  private final EventRecorder eventRecorder;

  @Autowired
  public ProductInstanceEventListener(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                      final EventRecorder eventRecorder) {
    super();
    this.logger = logger;
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_POST_PRODUCT_INSTANCE,
      subscription = EventConstants.DESTINATION
  )
  public void onCreateInstance(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String payload) {
    this.logger.debug("Product instance created.");
    this.eventRecorder.event(tenant, EventConstants.POST_PRODUCT_INSTANCE, payload, String.class);
  }

  @JmsListener(
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_ACTIVATE_PRODUCT_INSTANCE,
      subscription = EventConstants.DESTINATION
  )
  public void onActivateInstance(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String payload) {
    this.logger.debug("Product instance created.");
    this.eventRecorder.event(tenant, EventConstants.ACTIVATE_PRODUCT_INSTANCE, payload, String.class);
  }

  @JmsListener(
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_CLOSE_PRODUCT_INSTANCE,
      subscription = EventConstants.DESTINATION
  )
  public void onCloseInstance(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String payload) {
    this.logger.debug("Product instance created.");
    this.eventRecorder.event(tenant, EventConstants.CLOSE_PRODUCT_INSTANCE, payload, String.class);
  }

  @JmsListener(
      destination = EventConstants.DESTINATION,
      selector = EventConstants.SELECTOR_PUT_PRODUCT_INSTANCE,
      subscription = EventConstants.DESTINATION
  )
  public void onUpdateInstance(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.logger.debug("Product instance created.");
    this.eventRecorder.event(tenant, EventConstants.PUT_PRODUCT_INSTANCE, payload, String.class);
  }
}
