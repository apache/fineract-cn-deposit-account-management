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

import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.fineract.cn.accounting.api.v1.domain.Account;
import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.Action;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinition;
import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DepositApiDocumentation extends AbstractDepositAccountManagementTest {
  @Rule
  public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/doc/generated-snippets/test-deposit");

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Before
  public void setUp ( ) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation))
            .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
            .build();
  }

  @Test
  public void documentCreateAction ( ) throws Exception {
    final Action action = Fixture.action();

    Gson gson = new Gson();
    this.mockMvc.perform(post("/actions")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(action)))
            .andExpect(status().isAccepted())
            .andDo(document("document-create-action", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("Action identifier"),
                            fieldWithPath("name").description("Name of action"),
                            fieldWithPath("description").description("Description of action"),
                            fieldWithPath("transactionType").description("transaction type")
                    )));
  }

  @Test
  public void documentFetchActions ( ) throws Exception {
    this.mockMvc.perform(get("/actions")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-fetch-actions", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("[].identifier").description("Open"),
                            fieldWithPath("[].name").description("Account Opening"),
                            fieldWithPath("[].description").type("String").description("Description of Account Opening"),
                            fieldWithPath("[].transactionType").description("ACCO"),
                            fieldWithPath("[1].identifier").description("Transfer"),
                            fieldWithPath("[1].name").description("Account Transfer"),
                            fieldWithPath("[1].description").type("String").description("Description of Account Transfer"),
                            fieldWithPath("[1].transactionType").description("ACCT"),
                            fieldWithPath("[2].identifier").description("Close"),
                            fieldWithPath("[2].name").description("Account Closing"),
                            fieldWithPath("[2].description").type("String").description("Description of Account Closing"),
                            fieldWithPath("[2].transactionType").description("ACCC"),
                            fieldWithPath("[3].identifier").description("Deposit"),
                            fieldWithPath("[3].name").description("Cash Deposit"),
                            fieldWithPath("[3].description").type("String").description("Description of Cash Deposit"),
                            fieldWithPath("[3].transactionType").description("CDPT"),
                            fieldWithPath("[4].identifier").description("Withdraw"),
                            fieldWithPath("[4].name").description("Cash Withdraw"),
                            fieldWithPath("[4].description").type("String").description("Description of Cash Withdraw"),
                            fieldWithPath("[4].transactionType").description("CWDL")
                    )));
  }

  @Test
  public void documentCreateProductDefinition ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    Gson gson = new Gson();
    this.mockMvc.perform(post("/definitions")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(productDefinition)))
            .andExpect(status().isAccepted())
            .andDo(document("document-create-product-definition", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("type").description("Type of transaction"),
                            fieldWithPath("identifier").description("Action identifier"),
                            fieldWithPath("name").description("Name of action"),
                            fieldWithPath("description").description("Description of action"),
                            fieldWithPath("currency.code").description("Currency's code"),
                            fieldWithPath("currency.name").description("Currency's name"),
                            fieldWithPath("currency.sign").description("Currency's sign"),
                            fieldWithPath("currency.scale").type("Integer").description("Currency's scale"),
                            fieldWithPath("minimumBalance").type("Double").description("Minimum Balance"),
                            fieldWithPath("equityLedgerIdentifier").description("Equity Ledger Identifier"),
                            fieldWithPath("cashAccountIdentifier").description("Cash Account"),
                            fieldWithPath("expenseAccountIdentifier").description("Expense Account"),
                            fieldWithPath("accrueAccountIdentifier").description("Accrue Account"),
                            fieldWithPath("interest").type("Double").description("Interest"),
                            fieldWithPath("term.period").type("Integer").description("Term period"),
                            fieldWithPath("term.timeUnit").type("TimeUnit").description("Term time unit"),
                            fieldWithPath("term.interestPayable").description("Term interest payable"),
                            fieldWithPath("charges[].actionIdentifier").description("Charge first action"),
                            fieldWithPath("charges[].incomeAccountIdentifier").description("first Charge income account"),
                            fieldWithPath("charges[].name").description("Name of first charge"),
                            fieldWithPath("charges[].proportional").description("Is charge proportional ?"),
                            fieldWithPath("charges[].amount").type("Double").description("Amount of first charge"),
                            fieldWithPath("charges[1].actionIdentifier").description("Charge second action"),
                            fieldWithPath("charges[1].incomeAccountIdentifier").description("Charge income account"),
                            fieldWithPath("charges[1].name").description("Name of second charge"),
                            fieldWithPath("charges[1].proportional").description("Is charge proportional ?"),
                            fieldWithPath("charges[1].amount").type("Double").description("Amount of second charge"),
                            fieldWithPath("flexible").description("is product definition flexible ?")
                    )));
  }

  @Test
  public void documentFindProductDefinition ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    this.mockMvc.perform(get("/definitions/" + productDefinition.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-find-product-definition", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("type").description("Type of transaction"),
                            fieldWithPath("identifier").description("Action identifier"),
                            fieldWithPath("name").description("Name of action"),
                            fieldWithPath("description").description("Description of action"),
                            fieldWithPath("currency.code").description("Currency's code"),
                            fieldWithPath("currency.name").description("Currency's name"),
                            fieldWithPath("currency.sign").description("Currency's sign"),
                            fieldWithPath("currency.scale").type("Integer").description("Currency's scale"),
                            fieldWithPath("minimumBalance").type("Double").description("Minimum Balance"),
                            fieldWithPath("equityLedgerIdentifier").description("Equity Ledger Identifier"),
                            fieldWithPath("cashAccountIdentifier").description("Cash Account"),
                            fieldWithPath("expenseAccountIdentifier").description("Expense Account"),
                            fieldWithPath("accrueAccountIdentifier").description("Accrue Account"),
                            fieldWithPath("interest").type("Double").description("Interest"),
                            fieldWithPath("term.period").type("Integer").description("Term period"),
                            fieldWithPath("term.timeUnit").type("TimeUnit").description("Term time unit"),
                            fieldWithPath("term.interestPayable").description("Term interest payable"),
                            fieldWithPath("charges[].actionIdentifier").description("Charge first action"),
                            fieldWithPath("charges[].incomeAccountIdentifier").description("first Charge income account"),
                            fieldWithPath("charges[].name").description("Name of first charge"),
                            fieldWithPath("charges[].proportional").description("Is charge proportional ?"),
                            fieldWithPath("charges[].amount").type("Double").description("Amount of first charge"),
                            fieldWithPath("charges[].description").type("String").description("Description of first charge"),
                            fieldWithPath("charges[1].actionIdentifier").description("Charge second action"),
                            fieldWithPath("charges[1].incomeAccountIdentifier").description("Charge income account"),
                            fieldWithPath("charges[1].name").description("Name of second charge"),
                            fieldWithPath("charges[1].proportional").description("Is charge proportional ?"),
                            fieldWithPath("charges[1].description").type("String").description("Description of second charge"),
                            fieldWithPath("charges[1].amount").type("Double").description("Amount of second charge"),
                            fieldWithPath("flexible").description("Is product definition flexible ?"),
                            fieldWithPath("active").description("Is product definition active ?")
                    )));
  }

  @Test
  public void documentFetchProductDefinitions ( ) throws Exception {
    final ProductDefinition productDefinitionOne = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinitionOne);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinitionOne.getIdentifier());

    final ProductDefinition productDefinitionTwo = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinitionTwo);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinitionTwo.getIdentifier());

    this.mockMvc.perform(get("/definitions")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-fetch-product-definitions", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("[].type").description("Type of transaction"),
                            fieldWithPath("[].identifier").description("Action identifier"),
                            fieldWithPath("[].name").description("Name of action"),
                            fieldWithPath("[].description").description("Description of action"),
                            fieldWithPath("[].currency.code").description("Currency's code"),
                            fieldWithPath("[].currency.name").description("Currency's name"),
                            fieldWithPath("[].currency.sign").description("Currency's sign"),
                            fieldWithPath("[].currency.scale").type("Integer").description("Currency's scale"),
                            fieldWithPath("[].minimumBalance").type("Double").description("Minimum Balance"),
                            fieldWithPath("[].equityLedgerIdentifier").description("Equity Ledger Identifier"),
                            fieldWithPath("[].cashAccountIdentifier").description("Cash Account"),
                            fieldWithPath("[].expenseAccountIdentifier").description("Expense Account"),
                            fieldWithPath("[].accrueAccountIdentifier").description("Accrue Account"),
                            fieldWithPath("[].interest").type("Double").description("Interest"),
                            fieldWithPath("[].term.period").type("Integer").description("Term period"),
                            fieldWithPath("[].term.timeUnit").type("TimeUnit").description("Term time unit"),
                            fieldWithPath("[].term.interestPayable").description("Term interest payable"),
                            fieldWithPath("[].charges[].incomeAccountIdentifier").description("first Charge income account"),
                            fieldWithPath("[].charges[].name").description("Name of first charge"),
                            fieldWithPath("[].charges[].proportional").description("Is charge proportional ?"),
                            fieldWithPath("[].charges[].amount").type("Double").description("Amount of first charge"),
                            fieldWithPath("[].charges[].description").type("String").description("Description of first charge"),
                            fieldWithPath("[].charges[1].actionIdentifier").description("Charge second action"),
                            fieldWithPath("[].charges[1].incomeAccountIdentifier").description("Charge income account"),
                            fieldWithPath("[].charges[1].name").description("Name of second charge"),
                            fieldWithPath("[].charges[1].proportional").description("Is charge proportional ?"),
                            fieldWithPath("[].charges[1].description").type("String").description("Description of second charge"),
                            fieldWithPath("[].charges[1].amount").type("Double").description("Amount of second charge"),
                            fieldWithPath("[].flexible").description("Is product definition flexible ?"),
                            fieldWithPath("[1].active").description("Is product definition active ?"),
                            fieldWithPath("[1].type").description("Type of transaction"),
                            fieldWithPath("[1].identifier").description("Action identifier"),
                            fieldWithPath("[1].name").description("Name of action"),
                            fieldWithPath("[1].description").description("Description of action"),
                            fieldWithPath("[1].currency.code").description("Currency's code"),
                            fieldWithPath("[1].currency.name").description("Currency's name"),
                            fieldWithPath("[1].currency.sign").description("Currency's sign"),
                            fieldWithPath("[1].currency.scale").type("Integer").description("Currency's scale"),
                            fieldWithPath("[1].minimumBalance").type("Double").description("Minimum Balance"),
                            fieldWithPath("[1].equityLedgerIdentifier").description("Equity Ledger Identifier"),
                            fieldWithPath("[1].cashAccountIdentifier").description("Cash Account"),
                            fieldWithPath("[1].expenseAccountIdentifier").description("Expense Account"),
                            fieldWithPath("[1].accrueAccountIdentifier").description("Accrue Account"),
                            fieldWithPath("[1].interest").type("Double").description("Interest"),
                            fieldWithPath("[1].term.period").type("Integer").description("Term period"),
                            fieldWithPath("[1].term.timeUnit").type("TimeUnit").description("Term time unit"),
                            fieldWithPath("[1].term.interestPayable").description("Term interest payable"),
                            fieldWithPath("[1].charges[].actionIdentifier").description("Charge first action"),
                            fieldWithPath("[1].charges[].incomeAccountIdentifier").description("first Charge income account"),
                            fieldWithPath("[1].charges[].name").description("Name of first charge"),
                            fieldWithPath("[1].charges[].proportional").description("Is charge proportional ?"),
                            fieldWithPath("[1].charges[].amount").type("Double").description("Amount of first charge"),
                            fieldWithPath("[1].charges[].description").type("String").description("Description of first charge"),
                            fieldWithPath("[1].charges[1].actionIdentifier").description("Charge second action"),
                            fieldWithPath("[1].charges[1].incomeAccountIdentifier").description("Charge income account"),
                            fieldWithPath("[1].charges[1].name").description("Name of second charge"),
                            fieldWithPath("[1].charges[1].proportional").description("Is charge proportional ?"),
                            fieldWithPath("[1].charges[1].description").type("String").description("Description of second charge"),
                            fieldWithPath("[1].charges[1].amount").type("Double").description("Amount of second charge"),
                            fieldWithPath("[1].flexible").description("Is product definition flexible ?"),
                            fieldWithPath("[1].active").description("Is product definition active ?")
                    )));
  }

  @Test
  public void documentUpdateProductDefinition ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinition newProductDefinition = super.depositAccountManager.findProductDefinition(productDefinition.getIdentifier());
    newProductDefinition.setFlexible(Boolean.TRUE);
    newProductDefinition.setActive(Boolean.TRUE);

    Gson serializer = new Gson();
    this.mockMvc.perform(put("/definitions/" + productDefinition.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serializer.toJson(newProductDefinition))
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-update-product-definition", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("type").description("Type of transaction"),
                            fieldWithPath("identifier").description("Action identifier"),
                            fieldWithPath("name").description("Name of action"),
                            fieldWithPath("description").description("Description of action"),
                            fieldWithPath("currency.code").description("Currency's code"),
                            fieldWithPath("currency.name").description("Currency's name"),
                            fieldWithPath("currency.sign").description("Currency's sign"),
                            fieldWithPath("currency.scale").type("Integer").description("Currency's scale"),
                            fieldWithPath("minimumBalance").type("Double").description("Minimum Balance"),
                            fieldWithPath("equityLedgerIdentifier").description("Equity Ledger Identifier"),
                            fieldWithPath("cashAccountIdentifier").description("Cash Account"),
                            fieldWithPath("expenseAccountIdentifier").description("Expense Account"),
                            fieldWithPath("accrueAccountIdentifier").description("Accrue Account"),
                            fieldWithPath("interest").type("Double").description("Interest"),
                            fieldWithPath("term.period").type("Integer").description("Term period"),
                            fieldWithPath("term.timeUnit").type("TimeUnit").description("Term time unit"),
                            fieldWithPath("term.interestPayable").description("Term interest payable"),
                            fieldWithPath("charges[].actionIdentifier").description("Charge first action"),
                            fieldWithPath("charges[].incomeAccountIdentifier").description("first Charge income account"),
                            fieldWithPath("charges[].name").description("Name of first charge"),
                            fieldWithPath("charges[].proportional").description("Is charge proportional ?"),
                            fieldWithPath("charges[].amount").type("Double").description("Amount of first charge"),
                            fieldWithPath("charges[1].actionIdentifier").description("Charge second action"),
                            fieldWithPath("charges[1].incomeAccountIdentifier").description("Charge income account"),
                            fieldWithPath("charges[1].name").description("Name of second charge"),
                            fieldWithPath("charges[1].proportional").description("Is charge proportional ?"),
                            fieldWithPath("charges[1].amount").type("Double").description("Amount of second charge"),
                            fieldWithPath("flexible").description("Is product definition flexible ?"),
                            fieldWithPath("active").description("Is product definition active ?")
                    )));
  }

  @Test
  public void documentDeleteProductDefinition ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    this.mockMvc.perform(delete("/definitions/" + productDefinition.getIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-delete-product-definition"));
  }

  @Test
  public void documentFindProductInstances ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final Account account = new Account();
    account.setBalance(1234.56D);

    final List <ProductInstance> productInstances =
            super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());

    this.mockMvc.perform(get("/definitions/" + productDefinition.getIdentifier() + "/instances")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-find-product-instances", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("[].customerIdentifier").description("Customer Identifier"),
                            fieldWithPath("[].productIdentifier").description("Product identifier"),
                            fieldWithPath("[].accountIdentifier").description("Account Identifier"),
                            fieldWithPath("[].alternativeAccountNumber").description("Alternative Account Number"),
                            fieldWithPath("[].beneficiaries").type("Set<String>").description("Set of beneficiaries"),
                            fieldWithPath("[].openedOn").description(""),
                            fieldWithPath("[].lastTransactionDate").description("Last transaction date"),
                            fieldWithPath("[].state").type("String").description("State of product instance"),
                            fieldWithPath("[].balance").type("Double").description("Balance")
                    )));
  }

  @Test
  public void documentActivateProductDefinition ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinitionCommand activateProductDefinitionCommand = new ProductDefinitionCommand();
    activateProductDefinitionCommand.setAction(ProductDefinitionCommand.Action.ACTIVATE.name());
    activateProductDefinitionCommand.setNote("Note" + RandomStringUtils.randomAlphanumeric(4));

    Gson serializer = new Gson();
    this.mockMvc.perform(post("/definitions/" + productDefinition.getIdentifier() + "/commands")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serializer.toJson(activateProductDefinitionCommand))
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-activate-product-definition", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("action").description("Action " +
                                    "\n +" +
                                    "*enum* _Action_ {\n" +
                                    "    ACTIVATE,\n + " +
                                    "    DEACTIVATE\n + " +
                                    "  }"),
                            fieldWithPath("note").description("Note")
                    )));
  }

  @Test
  public void documentDeactivateProductDefinition ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinitionCommand deactivateProductDefinitionCommand = new ProductDefinitionCommand();
    deactivateProductDefinitionCommand.setAction(ProductDefinitionCommand.Action.DEACTIVATE.name());
    deactivateProductDefinitionCommand.setNote("Note" + RandomStringUtils.randomAlphanumeric(4));

    Gson serializer = new Gson();
    this.mockMvc.perform(post("/definitions/" + productDefinition.getIdentifier() + "/commands")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serializer.toJson(deactivateProductDefinitionCommand))
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-deactivate-product-definition", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("action").description("Action " +
                                    "\n +" +
                                    "*enum* _Action_ {\n" +
                                    "    ACTIVATE,\n + " +
                                    "    DEACTIVATE\n + " +
                                    "  }"),
                            fieldWithPath("note").description("Note")
                    )));
  }

  @Test
  public void documentGetProductDefinitionCommands ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductDefinitionCommand deactivateProductDefinitionCommand = new ProductDefinitionCommand();
    deactivateProductDefinitionCommand.setAction(ProductDefinitionCommand.Action.DEACTIVATE.name());
    deactivateProductDefinitionCommand.setNote("Note" + RandomStringUtils.randomAlphanumeric(4));

    this.mockMvc.perform(get("/definitions/" + productDefinition.getIdentifier() + "/commands")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-get-product-definition-commands", preprocessResponse(prettyPrint())));
  }

  @Test
  public void documentCreateInstance ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    Gson serializer = new Gson();
    this.mockMvc.perform(post("/instances")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serializer.toJson(productInstance))
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-create-instance", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("customerIdentifier").description("Customer Identifier"),
                            fieldWithPath("productIdentifier").description("Product identifier"),
                            fieldWithPath("beneficiaries").type("Set<String>").description("Set of beneficiaries")
                    )));
  }

  @Test
  public void documentFindInstance ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List <ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    final ProductInstance foundProductInstance = productInstances.get(0);

    this.mockMvc.perform(get("/instances/" + foundProductInstance.getAccountIdentifier())
            .contentType(MediaType.ALL_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-find-instance", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("customerIdentifier").description("Customer Identifier"),
                            fieldWithPath("productIdentifier").description("Product identifier"),
                            fieldWithPath("accountIdentifier").description("Account Identifier"),
                            fieldWithPath("alternativeAccountNumber").type("String").description("Alternative account Number"),
                            fieldWithPath("beneficiaries").type("Set<String>").description("Set of beneficiaries"),
                            fieldWithPath("openedOn").type("String").description("Date product instance was opened"),
                            fieldWithPath("lastTransactionDate").type("String").description("Last transaction date"),
                            fieldWithPath("state").description("State of product"),
                            fieldWithPath("balance").type("Double").description("balance")
                    )));
  }

  @Test
  public void documentFetchInstances ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    this.mockMvc.perform(get("/instances")
            .param("customer", productInstance.getCustomerIdentifier())
            .contentType(MediaType.ALL_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-fetch-instances", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("[].customerIdentifier").description("Customer Identifier"),
                            fieldWithPath("[].productIdentifier").description("Product identifier"),
                            fieldWithPath("[].accountIdentifier").description("Account Identifier"),
                            fieldWithPath("[].alternativeAccountNumber").type("String").description("Alternative account Number"),
                            fieldWithPath("[].beneficiaries").type("Set<String>").description("Set of beneficiaries"),
                            fieldWithPath("[].openedOn").type("String").description("Date product instance was opened"),
                            fieldWithPath("[].state").description("State of product"),
                            fieldWithPath("[].balance").type("Double").description("balance"),
                            fieldWithPath("[].lastTransactionDate").type("String").description("Last Transaction Date")
                    )));
  }

  @Test
  public void documentFetchTransactionTypes ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    this.mockMvc.perform(get("/instances/transactiontypes")
            .param("customer", productInstance.getCustomerIdentifier())
            .contentType(MediaType.ALL_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-fetch-transaction-types", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("[].transactionType").type("String").description("Transaction Type")
                    )));
  }

  @Test
  public void documentActivateInstance ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List <ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    final ProductInstance foundProductInstance = productInstances.get(0);

    this.mockMvc.perform(post("/instances/" + foundProductInstance.getAccountIdentifier())
            .param("command", "ACTIVATE")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-activate-instance"));
  }

  @Test
  public void documentCloseInstance ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();
    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());
    final String openedOn = LocalDate.of(2017, 5, 27).toString();
    productInstance.setOpenedOn(openedOn);

    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List <ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    final ProductInstance foundProductInstance = productInstances.get(0);

    this.mockMvc.perform(post("/instances/" + foundProductInstance.getAccountIdentifier())
            .param("command", "CLOSE")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-close-instance"));
  }

  @Test
  public void documentTransactInstance ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();
    super.depositAccountManager.create(productDefinition);

    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());
    final String openedOn = LocalDate.of(2017, 4, 26).toString();
    productInstance.setOpenedOn(openedOn);

    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List <ProductInstance> productInstances = super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());
    final ProductInstance foundProductInstance = productInstances.get(0);

    this.mockMvc.perform(post("/instances/" + foundProductInstance.getAccountIdentifier())
            .param("command", "TRANSACTION")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-transact-instance"));
  }

  @Test
  public void documentUpdateInstance ( ) throws Exception {
    final ProductDefinition productDefinition = Fixture.productDefinition();

    super.depositAccountManager.create(productDefinition);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_DEFINITION, productDefinition.getIdentifier());

    final ProductInstance productInstance = Fixture.productInstance(productDefinition.getIdentifier());

    super.depositAccountManager.create(productInstance);
    super.eventRecorder.wait(EventConstants.POST_PRODUCT_INSTANCE, productInstance.getCustomerIdentifier());

    final List <ProductInstance> productInstances =
            super.depositAccountManager.findProductInstances(productDefinition.getIdentifier());

    final ProductInstance fetchedProductInstance = productInstances.get(0);
    final HashSet <String> newBeneficiaries = new HashSet <>(Arrays.asList("BeneficiaryOne", "BeneficiaryTwo"));

    fetchedProductInstance.setBeneficiaries(newBeneficiaries);

    final Account account = new Account();
    account.setIdentifier(fetchedProductInstance.getAccountIdentifier());
    account.setName(RandomStringUtils.randomAlphanumeric(256));
    account.setLedger(RandomStringUtils.randomAlphanumeric(32));
    account.setBalance(0.00D);

    Mockito.doAnswer(invocation -> account)
            .when(super.accountingServiceSpy).findAccount(fetchedProductInstance.getAccountIdentifier());

    Gson gson = new Gson();
    this.mockMvc.perform(put("/instances/" + fetchedProductInstance.getAccountIdentifier())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(fetchedProductInstance))
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isAccepted())
            .andDo(document("document-update-instance", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("customerIdentifier").description("Customer Identifier"),
                            fieldWithPath("productIdentifier").description("Product identifier"),
                            fieldWithPath("accountIdentifier").description("Account Identifier"),
                            fieldWithPath("beneficiaries").type("Set<String>").description("Set of beneficiaries"),
                            fieldWithPath("state").description("State of product")
                    )));
  }
}
