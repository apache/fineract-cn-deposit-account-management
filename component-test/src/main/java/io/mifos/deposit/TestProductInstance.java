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

import com.google.common.collect.Sets;
import io.mifos.accounting.api.v1.domain.Account;
import io.mifos.deposit.api.v1.EventConstants;
import io.mifos.deposit.api.v1.definition.domain.Charge;
import io.mifos.deposit.api.v1.definition.domain.ProductDefinition;
import io.mifos.deposit.api.v1.instance.ProductInstanceNotFoundException;
import io.mifos.deposit.api.v1.instance.ProductInstanceValidationException;
import io.mifos.deposit.api.v1.instance.domain.AvailableTransactionType;
import io.mifos.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TestProductInstance extends AbstractDepositAccountManagementTest {

  public TestProductInstance() {
    super();
  }

  @Test
  public void shouldCreateProductInstance() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List<ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    Assert.assertNotNull(productInstances);
    Assert.assertEquals(1, productInstances.size());
    final ProductInstance foundProductInstance = productInstances.get(0);

    Assert.assertEquals(productInstance.getCustomerIdentifier() + "." + productDefinition.getEquityLedgerIdentifier() + ".00001",
        foundProductInstance.getAccountIdentifier());
    Assert.assertFalse(foundProductInstance.getBeneficiaries().isEmpty());
  }

  @Test
  public void shouldActivateProductInstance() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List<ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    Assert.assertNotNull(productInstances);
    Assert.assertEquals(1, productInstances.size());
    final ProductInstance foundProductInstance = productInstances.get(0);

    super.depositAccountManager.postProductInstanceCommand(
        foundProductInstance.getAccountIdentifier(), EventConstants.ACTIVATE_PRODUCT_INSTANCE_COMMAND);

    Assert.assertTrue(
        super.eventRecorder.wait(EventConstants.ACTIVATE_PRODUCT_INSTANCE,
            foundProductInstance.getAccountIdentifier()));
  }

  @Test
  public void shouldCloseProductInstance() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List<ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    Assert.assertNotNull(productInstances);
    Assert.assertEquals(1, productInstances.size());
    final ProductInstance foundProductInstance = productInstances.get(0);

    super.depositAccountManager.postProductInstanceCommand(
        foundProductInstance.getAccountIdentifier(), EventConstants.CLOSE_PRODUCT_INSTANCE_COMMAND);

    Assert.assertTrue(
        super.eventRecorder.wait(EventConstants.CLOSE_PRODUCT_INSTANCE,
            foundProductInstance.getAccountIdentifier()));
  }

  @Test
  public void shouldUpdateProductInstance() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List<ProductInstance> productInstances =
        super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());

    Assert.assertEquals(1, productInstances.size());

    final ProductInstance fetchedProductInstance = productInstances.get(0);

    final HashSet<String> newBeneficiaries = new HashSet<>(Arrays.asList("one", "two"));

    fetchedProductInstance.setBeneficiaries(newBeneficiaries);

    final Account account = new Account();
    account.setIdentifier(fetchedProductInstance.getAccountIdentifier());
    account.setName(RandomStringUtils.randomAlphanumeric(256));
    account.setLedger(RandomStringUtils.randomAlphanumeric(32));
    account.setBalance(0.00D);

    Mockito.doAnswer(invocation -> account)
        .when(super.accountingServiceSpy).findAccount(fetchedProductInstance.getAccountIdentifier());

    super.depositAccountManager.changeProductInstance(fetchedProductInstance.getAccountIdentifier(),
        fetchedProductInstance);

    Assert.assertTrue(super.eventRecorder.wait(EventConstants.PUT_PRODUCT_INSTANCE,
        fetchedProductInstance.getAccountIdentifier()));
  }

  @Test(expected = ProductInstanceValidationException.class)
  public void shouldNotUpdateProductInstanceIdentifierMismatch() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    productInstance.setBeneficiaries(null);

    super.depositAccountManager.changeProductInstance("identifiermissmatch", productInstance);
  }

  @Test
  public void shouldNotUpdateProductInstanceNoChangeDetected() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List<ProductInstance> productInstances =
        super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());

    Assert.assertEquals(1, productInstances.size());

    final ProductInstance fetchedProductInstance = productInstances.get(0);

    super.depositAccountManager.changeProductInstance(fetchedProductInstance.getAccountIdentifier(),
        fetchedProductInstance);

    Assert.assertFalse(super.eventRecorder.wait(EventConstants.PUT_PRODUCT_INSTANCE,
        fetchedProductInstance.getAccountIdentifier()));
  }

  @Test
  public void shouldFindProductInstance() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final Account account = new Account();
    account.setBalance(1234.56D);

    final List<ProductInstance> productInstances =
        super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());

    Assert.assertEquals(1, productInstances.size());

    final ProductInstance fetchedProductInstance = productInstances.get(0);

    Mockito.doAnswer(invocation -> account)
        .when(super.accountingServiceSpy).findAccount(fetchedProductInstance.getAccountIdentifier());

    final ProductInstance foundProductInstance =
        super.depositAccountManager.findProductInstance(fetchedProductInstance.getAccountIdentifier());

    Assert.assertNotNull(foundProductInstance);
    Assert.assertEquals(account.getBalance(), foundProductInstance.getBalance());
  }

  @Test(expected = ProductInstanceNotFoundException.class)
  public void shouldNotFindProductInstanceNotFound() {
    super.depositAccountManager.findProductInstance(RandomStringUtils.randomAlphanumeric(32));
  }

  @Test
  public void shouldOpenAccountAfterUpdatingDefinition() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final Charge openingCharge = new Charge();
    openingCharge.setActionIdentifier("Open");
    openingCharge.setAmount(5.00D);
    openingCharge.setName("Opening Account Charge");
    openingCharge.setIncomeAccountIdentifier("10123");
    openingCharge.setProportional(Boolean.TRUE);

    productDefinition.setCharges(new HashSet<>(Arrays.asList(openingCharge)));

    super.depositAccountManager.changeProductDefinition(productDefinition.getIdentifier(), productDefinition);

    super.eventRecorder.wait(EventConstants.PUT_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final List<ProductInstance> productInstances = super.depositAccountManager.fetchProductInstances(productInstance.getCustomerIdentifier());
    final ProductInstance fetchedProductInstance = productInstances.get(0);

    super.depositAccountManager.postProductInstanceCommand(
        fetchedProductInstance.getAccountIdentifier(), EventConstants.ACTIVATE_PRODUCT_INSTANCE_COMMAND);

    Assert.assertTrue(
        super.eventRecorder.wait(EventConstants.ACTIVATE_PRODUCT_INSTANCE,
            fetchedProductInstance.getAccountIdentifier()));
  }

  @Test
  public void shouldFindAvailableTransactionTypes() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final Set<AvailableTransactionType> availableTransactionTypesBeforeActivation =
        super.depositAccountManager.fetchPossibleTransactionTypes(productInstance.getCustomerIdentifier());

    Assert.assertFalse(availableTransactionTypesBeforeActivation.isEmpty());
    Assert.assertTrue(availableTransactionTypesBeforeActivation.size() == 1);
    final Optional<AvailableTransactionType> optionalTransactionType =
        availableTransactionTypesBeforeActivation.stream().findFirst();
    Assert.assertTrue(optionalTransactionType.isPresent());
    Assert.assertEquals("ACCO", optionalTransactionType.get().getTransactionType());

    final List<ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    Assert.assertNotNull(productInstances);
    Assert.assertEquals(1, productInstances.size());
    final ProductInstance foundProductInstance = productInstances.get(0);

    super.depositAccountManager.postProductInstanceCommand(
        foundProductInstance.getAccountIdentifier(), EventConstants.ACTIVATE_PRODUCT_INSTANCE_COMMAND);

    super.eventRecorder.wait(EventConstants.ACTIVATE_PRODUCT_INSTANCE, foundProductInstance.getAccountIdentifier());

    final Set<AvailableTransactionType> availableTransactionTypesAfterActivation =
        super.depositAccountManager.fetchPossibleTransactionTypes(productInstance.getCustomerIdentifier());

    Assert.assertFalse(availableTransactionTypesAfterActivation.isEmpty());
    Assert.assertTrue(availableTransactionTypesAfterActivation.size() == 5);
    final HashSet<String> expectedTransactionTypes = Sets.newHashSet("ACCC", "ACCT", "CDPT", "CWDL", "CCHQ");
    availableTransactionTypesAfterActivation.forEach(availableTransactionType ->
        expectedTransactionTypes.remove(availableTransactionType.getTransactionType())
    );

    Assert.assertTrue(expectedTransactionTypes.isEmpty());
  }
}
