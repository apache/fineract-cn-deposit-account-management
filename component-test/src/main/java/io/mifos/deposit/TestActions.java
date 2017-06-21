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
package io.mifos.deposit;

import io.mifos.deposit.api.v1.EventConstants;
import io.mifos.deposit.api.v1.definition.ActionAlreadyExistsException;
import io.mifos.deposit.api.v1.definition.domain.Action;
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
  public void shouldNoCreateActionAlreadyExists() throws Exception {
    final Action action = Fixture.action();
    super.depositAccountManager.create(action);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_ACTION, action.getIdentifier());

    super.depositAccountManager.create(action);
  }
}
