--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

CREATE TABLE shed_product_definitions (
  id                         BIGSERIAL      NOT NULL,
  a_type                     VARCHAR(32)    NOT NULL,
  identifier                 VARCHAR(32)    NOT NULL,
  a_name                     VARCHAR(256)   NOT NULL,
  description                VARCHAR(2048)  NULL,
  minimum_balance            NUMERIC(15, 5) NULL,
  equity_ledger_identifier   VARCHAR(32)    NOT NULL,
  expense_account_identifier VARCHAR(32)    NOT NULL,
  interest                   NUMERIC(5, 2)  NULL,
  is_flexible                BOOLEAN        NOT NULL,
  is_active                  BOOLEAN        NOT NULL,
  created_on                 TIMESTAMP(3)   NOT NULL,
  created_by                 VARCHAR(32)    NOT NULL,
  last_modified_on           TIMESTAMP(3)   NULL,
  last_modified_by           VARCHAR(32)    NULL,
  CONSTRAINT shed_product_definitions_pk PRIMARY KEY (id),
  CONSTRAINT shed_prod_def_identifier_uq UNIQUE (identifier));

CREATE TABLE shed_currencies (
  id                    BIGSERIAL    NOT NULL,
  product_definition_id BIGINT       NOT NULL,
  a_code                VARCHAR(4)   NOT NULL,
  a_name                VARCHAR(256) NOT NULL,
  sign                  VARCHAR(4)   NOT NULL,
  scale                 INT          NOT NULL,
  CONSTRAINT shed_currencies_pk PRIMARY KEY (id),
  CONSTRAINT shed_currencies_prod_def_fk FOREIGN KEY (product_definition_id) REFERENCES shed_product_definitions (id));

CREATE TABLE shed_terms (
  id                    BIGSERIAL    NOT NULL,
  product_definition_id BIGINT       NOT NULL,
  period                INT          NOT NULL,
  time_unit             VARCHAR(32)  NOT NULL,
  interest_payable      VARCHAR(32)  NOT NULL,
  CONSTRAINT shed_terms_pk PRIMARY KEY (id),
  CONSTRAINT shed_terms_prod_def_fk FOREIGN KEY (product_definition_id) REFERENCES shed_product_definitions (id));

CREATE TABLE shed_actions (
  id               BIGSERIAL     NOT NULL,
  identifier       VARCHAR(32)   NOT NULL,
  a_name           VARCHAR(256)  NOT NULL,
  description      VARCHAR(2048) NULL,
  transaction_type VARCHAR(32)   NOT NULL,
  CONSTRAINT shed_actions_pk PRIMARY KEY (id),
  CONSTRAINT shed_actions_identifier_uq UNIQUE (identifier));

INSERT INTO shed_actions (identifier, a_name, transaction_type) VALUES ('Open', 'Account Opening', 'ACCO');

INSERT INTO shed_actions (identifier, a_name, transaction_type) VALUES ('Transfer', 'Account Transfer', 'ACCT');

INSERT INTO shed_actions (identifier, a_name, transaction_type)VALUES ('Close', 'Account Closing', 'ACCC');

INSERT INTO shed_actions (identifier, a_name, transaction_type) VALUES ('Deposit', 'Cash Deposit', 'CDPT');

INSERT INTO shed_actions (identifier, a_name, transaction_type) VALUES ('Withdraw', 'Cash Withdrawal', 'CWDL');

CREATE TABLE shed_charges (
  id                        BIGSERIAL     NOT NULL,
  action_id                 BIGINT        NOT NULL,
  product_definition_id     BIGINT        NOT NULL,
  income_account_identifier VARCHAR(32)   NOT NULL,
  a_name                    VARCHAR(256)  NOT NULL,
  description               VARCHAR(2048) NULL,
  proportional              BOOLEAN   NOT NULL,
  amount                    NUMERIC(5, 2)  NULL,
  CONSTRAINT shed_charges_pk PRIMARY KEY (id),
  CONSTRAINT shed_charges_actions_fk FOREIGN KEY (action_id) REFERENCES shed_actions (id),
  CONSTRAINT shed_charges_prod_def_fk FOREIGN KEY (product_definition_id) REFERENCES shed_product_definitions (id));

CREATE TABLE shed_commands (
  id                    BIGSERIAL     NOT NULL,
  product_definition_id BIGINT        NOT NULL,
  a_action              VARCHAR(256)  NOT NULL,
  note                  VARCHAR(2048) NULL,
  created_on            TIMESTAMP(3)  NOT NULL,
  created_by            VARCHAR(32)   NOT NULL,
  CONSTRAINT shed_commands_pk PRIMARY KEY (id),
  CONSTRAINT shed_commands_prod_def_fk FOREIGN KEY (product_definition_id) REFERENCES shed_product_definitions (id));

CREATE TABLE shed_product_instances (
  id                    BIGSERIAL    NOT NULL,
  customer_identifier   VARCHAR(32)  NOT NULL,
  product_definition_id BIGINT       NOT NULL,
  account_identifier    VARCHAR(32)  NOT NULL,
  a_state               VARCHAR(32)  NOT NULL,
  created_on            TIMESTAMP(3) NOT NULL,
  created_by            VARCHAR(32)  NOT NULL,
  last_modified_on      TIMESTAMP(3) NULL,
  last_modified_by      VARCHAR(32)  NULL,
  CONSTRAINT shed_product_instances_pk PRIMARY KEY (id),
  CONSTRAINT shed_prod_inst_identifier_uq UNIQUE (account_identifier),
  CONSTRAINT shed_prod_inst_prod_def_fk FOREIGN KEY (product_definition_id) REFERENCES shed_product_definitions (id));
