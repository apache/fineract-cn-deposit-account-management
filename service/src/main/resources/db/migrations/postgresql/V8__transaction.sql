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

CREATE TABLE shed_transactions (
  id                          bigserial       NOT NULL,
  identifier                  VARCHAR(40)    NOT NULL,
  account_identifier                  VARCHAR(32)  NOT NULL,
  routing_code                  VARCHAR(40)    NULL,
  external_id                  VARCHAR(40)     NULL,
  a_name                      VARCHAR(256)   NULL,
  description                 VARCHAR(1024)  NULL,
  transaction_type            VARCHAR(32)    NOT NULL,
  sub_txn_type                VARCHAR(32)  NULL,
  amount                      numeric(15,5) NOT NULL,
  fee_amount                  numeric(15,5)  NULL,
  state                       VARCHAR(32)    NOT NULL,
  customer_account_identifier VARCHAR(32)     NULL,
  payable_account_identifier  VARCHAR(32)    NULL,
  nostro_account_identifier   VARCHAR(32)    NULL,
  transaction_date            TIMESTAMP   NULL,
  expiration_date             TIMESTAMP   NULL,
  created_by                  VARCHAR(32)     NULL,
  created_on                  TIMESTAMP    NULL,
  last_modified_by            VARCHAR(32)    NULL,
  last_modified_on            TIMESTAMP   NULL,
  CONSTRAINT pk_shed_transactions PRIMARY KEY (id),
  CONSTRAINT uk_shed_transactions_id UNIQUE (identifier),
  CONSTRAINT shed_txn_sub_txn_type_fk FOREIGN KEY (sub_txn_type) REFERENCES shed_sub_tx_type (identifier)),
  CONSTRAINT shed_txn_prod_instance_fk FOREIGN KEY (account_identifier) REFERENCES shed_product_instances (account_identifier))
);