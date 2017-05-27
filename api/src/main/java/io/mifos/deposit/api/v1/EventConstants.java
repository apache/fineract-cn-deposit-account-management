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
package io.mifos.deposit.api.v1;

@SuppressWarnings("unused")
public interface EventConstants {

  String DESTINATION = "deposit-v1";
  String SELECTOR_NAME = "action";

  String INITIALIZE = "initialize";
  String SELECTOR_INITIALIZE = SELECTOR_NAME + " = '" + INITIALIZE + "'";

  String POST_PRODUCT_ACTION = "post-product-action";
  String SELECTOR_POST_PRODUCT_ACTION = SELECTOR_NAME + " = " + POST_PRODUCT_ACTION;

  String POST_PRODUCT_DEFINITION = "post-product-definition";
  String SELECTOR_POST_PRODUCT_DEFINITION = SELECTOR_NAME + " = '" + POST_PRODUCT_DEFINITION + "'";
  String POST_PRODUCT_DEFINITION_COMMAND = "post-product-definition-command";
  String SELECTOR_POST_PRODUCT_COMMAND = SELECTOR_NAME + " = '" + POST_PRODUCT_DEFINITION_COMMAND + "'";

  String POST_PRODUCT_INSTANCE = "post-product-instance";
  String SELECTOR_POST_PRODUCT_INSTANCE = SELECTOR_NAME + " = '" + POST_PRODUCT_INSTANCE + "'";
  String POST_PRODUCT_INSTANCE_STATE_CHANGE = "post-product-instance-state-change";
  String SELECTOR_POST_PRODUCT_INSTANCE_STATE_CHANGE = SELECTOR_NAME + " = '" + POST_PRODUCT_INSTANCE_STATE_CHANGE + "'";
}
