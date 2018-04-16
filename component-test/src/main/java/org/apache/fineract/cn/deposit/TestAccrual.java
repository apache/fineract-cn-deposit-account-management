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
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.api.v1.domain.Type;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.deposit.service.internal.repository.AccruedInterestEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.AccruedInterestRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.fineract.cn.accounting.api.v1.domain.Account;
import org.apache.fineract.cn.accounting.api.v1.domain.AccountType;
import org.apache.fineract.cn.api.util.ApiFactory;
import org.apache.fineract.cn.lang.DateConverter;
import org.apache.fineract.cn.rhythm.spi.v1.client.BeatListener;
import org.apache.fineract.cn.rhythm.spi.v1.domain.BeatPublish;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAccrual extends AbstractDepositAccountManagementTest {

  @Autowired
  private AccruedInterestRepository accruedInterestRepository;

  private BeatListener depositBeatListener;

  public TestAccrual() {
    super();
  }

  @Before
  public void prepBeatListener() {
    depositBeatListener = new ApiFactory(super.logger)
        .create(BeatListener.class, AbstractDepositAccountManagementTest.testEnvironment.serverURI());
  }

  @Test
  public void shouldAccrueInterest() throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();
    productDefinition.setType(Type.SAVINGS.name());
    productDefinition.setInterest(2.50D);
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

    final LocalDateTime dueDate = DateConverter.fromIsoString("2017-08-02T22:00:00.000Z");
    final BeatPublish beatPublish = new BeatPublish();
    beatPublish.setIdentifier(RandomStringUtils.randomAlphanumeric(32));
    beatPublish.setForTime(DateConverter.toIsoString(dueDate));
    this.depositBeatListener.publishBeat(beatPublish);

    super.eventRecorder.wait(EventConstants.INTEREST_ACCRUED, DateConverter.toIsoString(dueDate.toLocalDate()));

    final Optional<AccruedInterestEntity> optionalAccruedInterest =
        this.accruedInterestRepository.findByCustomerAccountIdentifier(foundProductInstance.getAccountIdentifier());

    Assert.assertTrue(optionalAccruedInterest.isPresent());
    final AccruedInterestEntity accruedInterestEntity = optionalAccruedInterest.get();
    final Double interest =
        accruedInterestEntity.getAmount()
            * DateConverter.fromIsoString(beatPublish.getForTime()).toLocalDate().lengthOfYear();

    final Double roundedInterest =
        BigDecimal.valueOf(interest).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();

    Assert.assertEquals(25.00D, roundedInterest, 0.00D);
  }
}
