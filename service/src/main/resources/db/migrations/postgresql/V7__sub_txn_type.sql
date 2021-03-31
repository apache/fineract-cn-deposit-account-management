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

CREATE TABLE shed_sub_tx_type (
	id bigserial NOT NULL,
	identifier varchar(32) NOT NULL,
	a_name varchar(256) NOT NULL,
	description varchar(2048) NULL,
	is_cash_payment bool NOT NULL,
	is_active  bool NOT null,
	order_position int8 NOT null,
	tran_type_enum int8  NULL,
	ledger_account_identifier varchar(32) NULL,
	CONSTRAINT shed_sub_tx_type_pk PRIMARY KEY (id),
	CONSTRAINT shed_sub_tx_type_identifier_uq UNIQUE (identifier)
);