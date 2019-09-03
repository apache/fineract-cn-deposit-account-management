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

ALTER TABLE shed_product_definitions ADD COLUMN cash_account_identifier VARCHAR(34) NULL;
ALTER TABLE shed_product_definitions ADD COLUMN accrue_account_identifier VARCHAR(34) NULL;
ALTER TABLE shed_product_definitions ALTER COLUMN equity_ledger_identifier TYPE VARCHAR(34), ALTER COLUMN equity_ledger_identifier SET NOT NULL;
ALTER TABLE shed_product_definitions ALTER COLUMN expense_account_identifier TYPE VARCHAR(34) ,ALTER COLUMN expense_account_identifier SET NOT NULL;

CREATE TABLE shed_accrued_interests (
  id                          BIGSERIAL      NOT NULL,
  accrue_account_identifier   VARCHAR(34)    NOT NULL,
  customer_account_identifier VARCHAR(34)    NOT NULL,
  amount                      NUMERIC(15, 5) NOT NULL,
  CONSTRAINT shed_accrued_interests_pk PRIMARY KEY (id),
  CONSTRAINT shed_accrued_interests_uq UNIQUE (accrue_account_identifier, customer_account_identifier));

CREATE TABLE shed_dividend_distributions (
  id                    BIGSERIAL         NOT NULL,
  product_definition_id BIGINT         NOT NULL,
  due_date              DATE           NOT NULL,
  rate                  NUMERIC(15, 5) NOT NULL,
  created_on            TIMESTAMP(3)   NOT NULL,
  created_by            VARCHAR(32)    NOT NULL,
  CONSTRAINT shed_dividend_distributions_pk PRIMARY KEY (id),
  CONSTRAINT shed_div_dist_prod_def_fk FOREIGN KEY (product_definition_id) REFERENCES shed_product_definitions (id));
