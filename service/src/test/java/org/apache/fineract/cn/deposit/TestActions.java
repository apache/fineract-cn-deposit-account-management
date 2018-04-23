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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestActions extends AbstractDepositAccountManagementTest {

  @Rule
  public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("src/doc/generated-snippets/test-actions");

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  final String path = "/deposit/v1";

  @Before
  public void setUp(){

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation))
            .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
            .build();
  }

  public TestActions() {
    super();
  }

  @Test
  public void shouldFetchDefaultActions() {
    final List<Action> actions = super.depositAccountManager.fetchActions();

    Assert.assertTrue(actions.size() >= 5);

    try
    {
      this.mockMvc.perform(get(path + "/actions")
              .accept(MediaType.ALL_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
              .andExpect(status().isNotFound());
    } catch (Exception exception){ exception.printStackTrace(); }
  }

  @Test
  public void shouldCreateAction() throws Exception {
    final Action action = Fixture.action();

    super.depositAccountManager.create(action);

    Assert.assertTrue(super.eventRecorder.wait(EventConstants.POST_PRODUCT_ACTION, action.getIdentifier()));

    this.mockMvc.perform(post(path + "/actions")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(action.getIdentifier()))
            .andExpect(status().isNotFound());
  }

  @Test(expected = ActionAlreadyExistsException.class)
  public void shouldNotCreateActionAlreadyExists() throws Exception {
    final Action action = Fixture.action();
    super.depositAccountManager.create(action);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_ACTION, action.getIdentifier());

    super.depositAccountManager.create(action);
  }
}
