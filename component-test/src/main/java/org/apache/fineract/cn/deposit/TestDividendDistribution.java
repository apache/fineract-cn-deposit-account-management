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
import org.apache.fineract.cn.deposit.api.v1.definition.domain.DividendDistribution;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.api.v1.domain.Type;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import java.util.List;
import org.apache.fineract.cn.accounting.api.v1.domain.Account;
import org.apache.fineract.cn.accounting.api.v1.domain.AccountType;
import org.apache.fineract.cn.accounting.api.v1.domain.JournalEntry;
import org.apache.fineract.cn.lang.DateOfBirth;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class TestDividendDistribution extends AbstractDepositAccountManagementTest {

  public TestDividendDistribution() {
    super();
  }

  @Test
  public void shouldDistributeDividend() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();
    productDefinition.setType(Type.SHARE.name());
    productDefinition.setInterest(null);
    productDefinition.setAccrueAccountIdentifier(null);
    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinitionCommand productDefinitionCommand = new ProductDefinitionCommand();
    productDefinitionCommand.setAction(ProductDefinitionCommand.Action.ACTIVATE.name());
    super.depositAccountManager.process(productDefinition.getIdentifier(), productDefinitionCommand);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION_COMMAND, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());
    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List<ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    Assert.assertNotNull(productInstances);
    Assert.assertEquals(1, productInstances.size());
    final ProductInstance foundProductInstance = productInstances.get(0);

    super.depositAccountManager.postProductInstanceCommand(
        foundProductInstance.getAccountIdentifier(), EventConstants.ACTIVATE_PRODUCT_INSTANCE_COMMAND);
    super.eventRecorder.wait(EventConstants.ACTIVATE_PRODUCT_INSTANCE, foundProductInstance.getAccountIdentifier());

    final Account shareAccount = new Account();
    shareAccount.setType(AccountType.EQUITY.name());
    shareAccount.setIdentifier(foundProductInstance.getAccountIdentifier());
    shareAccount.setBalance(1000.00D);

    Mockito
        .doAnswer(invocation -> shareAccount)
        .when(super.accountingServiceSpy).findAccount(shareAccount.getIdentifier());

    final DividendDistribution dividendDistribution = new DividendDistribution();
    final DateOfBirth dueDate = new DateOfBirth();
    dueDate.setYear(2017);
    dueDate.setMonth(7);
    dueDate.setDay(31);
    dividendDistribution.setDueDate(dueDate);
    dividendDistribution.setDividendRate("2.5");
    this.depositAccountManager.dividendDistribution(productDefinition.getIdentifier(), dividendDistribution);

    Assert.assertTrue(super.eventRecorder.wait(EventConstants.DIVIDEND_DISTRIBUTION, productDefinition.getIdentifier()));

    final List<DividendDistribution> dividendDistributions =
        super.depositAccountManager.fetchDividendDistributions(productDefinition.getIdentifier());

    Assert.assertEquals(1, dividendDistributions.size());
    Assert.assertTrue(dividendDistribution.equals(dividendDistributions.get(0)));

    Mockito.verify(super.accountingServiceSpy, Mockito.times(2)).post(Matchers.any(JournalEntry.class));
  }
}
