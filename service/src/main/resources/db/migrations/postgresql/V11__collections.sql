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


CREATE TABLE shed_collections (
  id                          bigserial       NOT null,
  transaction_date            TIMESTAMP   NULL,
  amount          			  numeric(15,5) NOT NULL,
  transport_fee_amount        numeric(15,5)  NULL,
  currency                    VARCHAR(10)   NULL,
  remarks                     VARCHAR(1024)  NULL,
  account_identifier 		  BIGINT  NULL,
  sub_txn_type_id			  BIGINT  NULL,
  status                      VARCHAR(20)      NOT NULL,
  c_reference                 VARCHAR(36)  NOT NULL,
  created_by                  VARCHAR(32)    NOT NULL,
  created_on                  TIMESTAMP   NOT NULL,
  last_modified_by            VARCHAR(32)    NULL,
  last_modified_on            TIMESTAMP   NULL,
  CONSTRAINT pk_shed_collections PRIMARY KEY (id),
  CONSTRAINT uk_shed_collections_ref UNIQUE (c_reference),
  CONSTRAINT fk_collection_sub_txn_type_id FOREIGN KEY (sub_txn_type_id) REFERENCES shed_sub_tx_type (id),
  CONSTRAINT fk_collection_account_identifier FOREIGN KEY (account_identifier) REFERENCES shed_product_instances (account_identifier)
);


CREATE TABLE shed_collections_inidividual (
  id                          bigserial      NOT NULL,
  collections_id              BIGINT         NOT NULL,
  account_identifier          BIGINT         NULL,
  account_external_id         VARCHAR(64)    NULL,
  amount                      numeric(15,5) NOT NULL,
  i_reference                 VARCHAR(36)   NOT NULL,
  CONSTRAINT pk_shed_collections_inidividual PRIMARY KEY (id),
  CONSTRAINT uk_shed_collections_inidividual_ref UNIQUE (i_reference),
  CONSTRAINT fk_indidual_collections FOREIGN KEY (collections_id) REFERENCES shed_collections (id),
  CONSTRAINT fk_ind_collections_account_identifier FOREIGN KEY (account_identifier) REFERENCES shed_product_instances (account_identifier)
);



CREATE TABLE shed_self_expiring_tokens (
  id                            bigserial      NOT NULL,
  token                         VARCHAR(10)   NOT NULL,
  token_expires_by              TIMESTAMP  NOT NULL,
  status                        VARCHAR(10)   NOT NULL,
  entity_type                   VARCHAR(36)   NOT NULL,
  entity_reference              VARCHAR(36)   NOT NULL,
  CONSTRAINT pk_shed_self_expiring_tokens PRIMARY KEY (id),
  CONSTRAINT uk_shed_self_expiring_tokens_token UNIQUE (token, status)
);
