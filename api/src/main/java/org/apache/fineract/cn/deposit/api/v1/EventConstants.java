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
package org.apache.fineract.cn.deposit.api.v1;

@SuppressWarnings("unused")
public interface EventConstants {

  String DESTINATION = "deposit-v1";
  String SELECTOR_NAME = "action";

  String INITIALIZE = "initialize";
  String SELECTOR_INITIALIZE = SELECTOR_NAME + " = '" + INITIALIZE + "'";

  String POST_PRODUCT_ACTION = "post-product-action";
  String SELECTOR_POST_PRODUCT_ACTION = SELECTOR_NAME + " = '" + POST_PRODUCT_ACTION + "'";

  String POST_PRODUCT_DEFINITION = "post-product-definition";
  String SELECTOR_POST_PRODUCT_DEFINITION = SELECTOR_NAME + " = '" + POST_PRODUCT_DEFINITION + "'";
  String PUT_PRODUCT_DEFINITION = "put-product-definition";
  String SELECTOR_PUT_PRODUCT_DEFINITION = SELECTOR_NAME + " = '" + PUT_PRODUCT_DEFINITION + "'";
  String DELETE_PRODUCT_DEFINITION = "delete-product-definition";
  String SELECTOR_DELETE_PRODUCT_DEFINITION = SELECTOR_NAME + " = '" + DELETE_PRODUCT_DEFINITION + "'";
  String POST_PRODUCT_DEFINITION_COMMAND = "post-product-definition-command";
  String SELECTOR_POST_PRODUCT_COMMAND = SELECTOR_NAME + " = '" + POST_PRODUCT_DEFINITION_COMMAND + "'";

  String POST_PRODUCT_INSTANCE = "post-product-instance";
  String SELECTOR_POST_PRODUCT_INSTANCE = SELECTOR_NAME + " = '" + POST_PRODUCT_INSTANCE + "'";
  String POST_PRODUCT_INSTANCE_STATE_CHANGE = "post-product-instance-state-change";
  String SELECTOR_POST_PRODUCT_INSTANCE_STATE_CHANGE = SELECTOR_NAME + " = '" + POST_PRODUCT_INSTANCE_STATE_CHANGE + "'";
  String ACTIVATE_PRODUCT_INSTANCE = "activate-process-instance";
  String SELECTOR_ACTIVATE_PRODUCT_INSTANCE = SELECTOR_NAME + " = '" + ACTIVATE_PRODUCT_INSTANCE + "'";
  String CLOSE_PRODUCT_INSTANCE = "close-process-instance";
  String SELECTOR_CLOSE_PRODUCT_INSTANCE = SELECTOR_NAME + " = '" + CLOSE_PRODUCT_INSTANCE + "'";
  String PUT_PRODUCT_INSTANCE = "put-product-instance";
  String SELECTOR_PUT_PRODUCT_INSTANCE = SELECTOR_NAME + " = '" + PUT_PRODUCT_INSTANCE + "'";

  String ACTIVATE_PRODUCT_INSTANCE_COMMAND = "ACTIVATE";
  String CLOSE_PRODUCT_INSTANCE_COMMAND = "CLOSE";
  String PRODUCT_INSTANCE_TRANSACTION = "TRANSACTION";

  String DIVIDEND_DISTRIBUTION = "dividend-distribution";
  String SELECTOR_DIVIDEND_DISTRIBUTION = SELECTOR_NAME + " = '" + DIVIDEND_DISTRIBUTION + "'";
  String INTEREST_ACCRUED = "interest-accrued";
  String SELECTOR_INTEREST_ACCRUED = SELECTOR_NAME + " = '" + INTEREST_ACCRUED + "'";
  String INTEREST_PAYED = "interest-payed";
  String SELECTOR_INTEREST_PAYED = SELECTOR_NAME + " = '" + INTEREST_PAYED + "'";
}
