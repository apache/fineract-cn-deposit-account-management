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
import org.apache.fineract.cn.deposit.api.v1.client.DepositAccountManager;
import org.apache.fineract.cn.deposit.service.DepositAccountManagementConfiguration;
import org.apache.fineract.cn.deposit.service.internal.service.helper.AccountingService;
import org.apache.fineract.cn.deposit.service.internal.service.helper.RhythmService;
import org.apache.fineract.cn.anubis.test.v1.TenantApplicationSecurityEnvironmentTestRule;
import org.apache.fineract.cn.api.context.AutoUserContext;
import org.apache.fineract.cn.lang.ApplicationName;
import org.apache.fineract.cn.test.fixture.TenantDataStoreContextTestRule;
import org.apache.fineract.cn.test.listener.EnableEventRecording;
import org.apache.fineract.cn.test.listener.EventRecorder;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    classes = {AbstractDepositAccountManagementTest.TestConfiguration.class}
)
public abstract class AbstractDepositAccountManagementTest extends SuiteTestEnvironment {
  private static final String TEST_USER = "shed";
  public static final String TEST_LOGGER = "test-logger";

  @ClassRule
  public final static TenantDataStoreContextTestRule tenantDataStoreContext = TenantDataStoreContextTestRule.forRandomTenantName(cassandraInitializer, postgreSQLInitializer);

  @Rule
  public final TenantApplicationSecurityEnvironmentTestRule tenantApplicationSecurityEnvironment
      = new TenantApplicationSecurityEnvironmentTestRule(testEnvironment, this::waitForInitialize);

  @Autowired
  @Qualifier(TEST_LOGGER)
  protected Logger logger;

  @Autowired
  DepositAccountManager depositAccountManager;

  @Autowired
  private ApplicationName applicationName;

  @Autowired
  EventRecorder eventRecorder;

  @MockBean
  AccountingService accountingServiceSpy;

  @MockBean
  RhythmService rhythmService;

  private AutoUserContext autoUserContext;

  AbstractDepositAccountManagementTest() {
    super();
  }

  @Before
  public void prepTest() throws Exception {
    this.autoUserContext = this.tenantApplicationSecurityEnvironment.createAutoUserContext(AbstractDepositAccountManagementTest.TEST_USER);
  }

  @After
  public void cleanTest() throws Exception {
    this.autoUserContext.close();
  }

  public boolean waitForInitialize() {
    try {
      final String version = this.applicationName.getVersionString();
      this.logger.info("Waiting on initialize event for version: {}.", version);
      return this.eventRecorder.wait(EventConstants.INITIALIZE, version);
    } catch (final InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  @Configuration
  @EnableEventRecording
  @EnableFeignClients(basePackages = {"org.apache.fineract.cn.deposit.api.v1"})
  @RibbonClient(name = APP_NAME)
  @Import({DepositAccountManagementConfiguration.class})
  @ComponentScan("org.apache.fineract.cn.deposit.listener")
  public static class TestConfiguration {
    public TestConfiguration() {
      super();
    }

    @Bean(name= TEST_LOGGER)
    public Logger logger() {
      return LoggerFactory.getLogger(TEST_LOGGER);
    }
  }
}

