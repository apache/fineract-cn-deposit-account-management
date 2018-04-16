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
package org.apache.fineract.cn.deposit;

import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.api.v1.definition.ActionAlreadyExistsException;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Action;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestActions extends AbstractDepositAccountManagementTest {

  public TestActions() {
    super();
  }

  @Test
  public void shouldFetchDefaultActions() {
    final List<Action> actions = super.depositAccountManager.fetchActions();

    Assert.assertTrue(actions.size() >= 5);
  }

  @Test
  public void shouldCreateAction() throws Exception {
    final Action action = Fixture.action();

    super.depositAccountManager.create(action);

    Assert.assertTrue(super.eventRecorder.wait(EventConstants.POST_PRODUCT_ACTION, action.getIdentifier()));
  }

  @Test(expected = ActionAlreadyExistsException.class)
  public void shouldNotCreateActionAlreadyExists() throws Exception {
    final Action action = Fixture.action();
    super.depositAccountManager.create(action);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_ACTION, action.getIdentifier());

    super.depositAccountManager.create(action);
  }
}
