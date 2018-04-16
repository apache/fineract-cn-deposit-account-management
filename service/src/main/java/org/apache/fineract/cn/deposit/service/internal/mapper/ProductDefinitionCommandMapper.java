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
package org.apache.fineract.cn.deposit.service.internal.mapper;

import org.apache.fineract.cn.deposit.api.v1.definition.domain.ProductDefinitionCommand;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductDefinitionCommandEntity;
import org.apache.fineract.cn.lang.DateConverter;

public class ProductDefinitionCommandMapper {

  private ProductDefinitionCommandMapper() {
    super();
  }

  public static ProductDefinitionCommandEntity map(final ProductDefinitionCommand command) {
    final ProductDefinitionCommandEntity entity = new ProductDefinitionCommandEntity();
    entity.setAction(command.getAction());
    entity.setNote(command.getNote());
    if (command.getCreatedBy() != null) {
      entity.setCreatedBy(command.getCreatedBy());
      entity.setCreatedOn(DateConverter.fromIsoString(command.getCreatedOn()));
    }

    return entity;
  }

  public static ProductDefinitionCommand map(final ProductDefinitionCommandEntity entity) {
    final ProductDefinitionCommand command = new ProductDefinitionCommand();
    command.setAction(entity.getAction());
    command.setNote(entity.getNote());
    command.setCreatedBy(entity.getCreatedBy());
    command.setCreatedOn(DateConverter.toIsoString(entity.getCreatedOn()));

    return command;
  }
}
