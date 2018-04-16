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
import org.apache.fineract.cn.deposit.api.v1.definition.ProductDefinitionAlreadyExistsException;
import org.apache.fineract.cn.deposit.api.v1.definition.ProductDefinitionValidationException;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
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


  @Test(expected = ProductDefinitionAlreadyExistsException.class)
  public void shouldNotCreateProductDefinitionAlreadyExists() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    super.depositAccountManager.create(productDefinition);
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

  @Test
  public void shouldAllowCommandWithNullNote() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinitionCommand productDefinitionCommand = new ProductDefinitionCommand();
    productDefinitionCommand.setAction(ProductDefinitionCommand.Action.DEACTIVATE.name());

    super.depositAccountManager.process(productDefinition.getIdentifier(), productDefinitionCommand);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION_COMMAND, productDefinition.getIdentifier());

    final ProductDefinition fetchProductDefinition = super.depositAccountManager.findProductDefinition(productDefinition.getIdentifier());

    Assert.assertFalse(fetchProductDefinition.getActive());
  }

  @Test
  public void shouldDeleteProductDefinition() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    super.depositAccountManager.deleteProductDefinition(productDefinition.getIdentifier());

    Assert.assertTrue(super.eventRecorder.wait(EventConstants.DELETE_PRODUCT_DEFINITION, productDefinition.getIdentifier()));
  }

  @Test
  public void shouldDeleteProductDefinitionEvenWhenActed() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinitionCommand activateProduct = new ProductDefinitionCommand();
    activateProduct.setAction(ProductDefinitionCommand.Action.ACTIVATE.name());
    activateProduct.setNote(RandomStringUtils.randomAlphanumeric(2048));

    super.depositAccountManager.process(productDefinition.getIdentifier(), activateProduct);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION_COMMAND, productDefinition.getIdentifier());

    final ProductDefinitionCommand deactivateProduct = new ProductDefinitionCommand();
    deactivateProduct.setAction(ProductDefinitionCommand.Action.DEACTIVATE.name());
    deactivateProduct.setNote(RandomStringUtils.randomAlphanumeric(2048));

    super.depositAccountManager.process(productDefinition.getIdentifier(), deactivateProduct);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION_COMMAND, productDefinition.getIdentifier());

    super.depositAccountManager.deleteProductDefinition(productDefinition.getIdentifier());

    Assert.assertTrue(super.eventRecorder.wait(EventConstants.DELETE_PRODUCT_DEFINITION, productDefinition.getIdentifier()));
  }

  @Test(expected = ProductDefinitionValidationException.class)
  public void shouldNotDeleteProductDefinitionInstanceExists() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    this.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    super.depositAccountManager.deleteProductDefinition(productDefinition.getIdentifier());
  }

  @Test
  public void shouldUpdateProductDefinition() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinition newProductDefinition = Fixture.productDefinition();

    newProductDefinition.setIdentifier(productDefinition.getIdentifier());

    super.depositAccountManager.changeProductDefinition(newProductDefinition.getIdentifier(), newProductDefinition);

    Assert.assertTrue(super.eventRecorder.wait(EventConstants.PUT_PRODUCT_DEFINITION, newProductDefinition.getIdentifier()));

    final ProductDefinition fetchedProductDefinition = super.depositAccountManager.findProductDefinition(newProductDefinition.getIdentifier());

    Assert.assertNotNull(fetchedProductDefinition);
  }

  @Test(expected = ProductDefinitionValidationException.class)
  public void shouldNotUpdateProductDefinitionIdentifierMismatch() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    super.depositAccountManager.changeProductDefinition(productDefinition.getIdentifier(), Fixture.productDefinition());
  }

  @Test(expected = ProductDefinitionValidationException.class)
  public void shouldNotUpdateProductDefinitionInterestNotFlexible() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();
    productDefinition.setFlexible(Boolean.FALSE);
    productDefinition.setInterest(5.00D);

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());
    productDefinition.setInterest(10.0D);

    super.depositAccountManager.changeProductDefinition(productDefinition.getIdentifier(), Fixture.productDefinition());
  }
}
