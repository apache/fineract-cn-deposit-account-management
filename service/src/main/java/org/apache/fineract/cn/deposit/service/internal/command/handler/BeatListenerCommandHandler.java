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
package org.apache.fineract.cn.deposit.service.internal.command.handler;

import org.apache.fineract.cn.deposit.service.internal.command.AccrualCommand;
import org.apache.fineract.cn.deposit.service.internal.command.BeatListenerCommand;
import org.apache.fineract.cn.deposit.service.internal.command.PayInterestCommand;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.domain.CommandCallback;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.DateConverter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;

@Aggregate
public class BeatListenerCommandHandler {

  private final CommandGateway commandGateway;

  @Autowired
  public BeatListenerCommandHandler(final CommandGateway commandGateway) {
    super();
    this.commandGateway = commandGateway;
  }

  @Transactional
  @CommandHandler
  public void process(final BeatListenerCommand beatListenerCommand) {
    try {
      final LocalDateTime dueDate = DateConverter.fromIsoString(beatListenerCommand.beatPublish().getForTime());
      final CommandCallback<String> commandCallback =
          this.commandGateway.process(new AccrualCommand(dueDate.toLocalDate()), String.class);

      final String date = commandCallback.get();
      this.commandGateway.process(new PayInterestCommand(DateConverter.dateFromIsoString(date)));

    } catch (Exception ex) {
      throw ServiceException.internalError("Could not handle beat: {0}", ex.getMessage());
    }
  }
}
