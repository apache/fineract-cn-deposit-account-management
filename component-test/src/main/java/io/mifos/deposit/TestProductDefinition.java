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
import io.mifos.deposit.api.v1.definition.domain.ProductDefinition;
import io.mifos.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestProductDefinition extends AbstractDepositAccountManagementTest {

  public TestProductDefinition() {
    super();
  }

  @Test
  public void shouldCreateProductDefinition() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());
  }

  @Test
  public void shouldFindProductDefinition() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinition fetchedProductDefinition = super.depositAccountManager.findProductDefinition(productDefinition.getIdentifier());

    Assert.assertNotNull(fetchedProductDefinition);
    Assert.assertNotNull(fetchedProductDefinition.getCharges());
    Assert.assertEquals(2, fetchedProductDefinition.getCharges().size());
    Assert.assertNotNull(fetchedProductDefinition.getCurrency());
    Assert.assertNotNull(fetchedProductDefinition.getTerm());
    Assert.assertFalse(fetchedProductDefinition.getActive());
  }

  @Test
  public void shouldActivateProductDefinition() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinitionCommand productDefinitionCommand = new ProductDefinitionCommand();
    productDefinitionCommand.setAction(ProductDefinitionCommand.Action.ACTIVATE.name());
    productDefinitionCommand.setNote(RandomStringUtils.randomAlphanumeric(2048));

    super.depositAccountManager.process(productDefinition.getIdentifier(), productDefinitionCommand);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION_COMMAND, productDefinition.getIdentifier());

    final ProductDefinition fetchedProductDefinition = super.depositAccountManager.findProductDefinition(productDefinition.getIdentifier());

    Assert.assertTrue(fetchedProductDefinition.getActive());
  }

  @Test
  public void shouldDeactivateProductDefinition() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinitionCommand productDefinitionCommand = new ProductDefinitionCommand();
    productDefinitionCommand.setAction(ProductDefinitionCommand.Action.DEACTIVATE.name());
    productDefinitionCommand.setNote(RandomStringUtils.randomAlphanumeric(2048));

    super.depositAccountManager.process(productDefinition.getIdentifier(), productDefinitionCommand);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION_COMMAND, productDefinition.getIdentifier());

    final ProductDefinition fetchProductDefinition = super.depositAccountManager.findProductDefinition(productDefinition.getIdentifier());

    Assert.assertFalse(fetchProductDefinition.getActive());
  }
}
