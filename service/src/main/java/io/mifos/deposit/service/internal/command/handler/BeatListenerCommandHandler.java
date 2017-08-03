/*
 * Copyright 2017 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.deposit.service.internal.command.handler;

import io.mifos.core.command.annotation.Aggregate;
import io.mifos.core.command.annotation.CommandHandler;
import io.mifos.core.command.domain.CommandCallback;
import io.mifos.core.command.gateway.CommandGateway;
import io.mifos.core.lang.DateConverter;
import io.mifos.core.lang.ServiceException;
import io.mifos.deposit.service.internal.command.AccrualCommand;
import io.mifos.deposit.service.internal.command.BeatListenerCommand;
import io.mifos.deposit.service.internal.command.PayInterestCommand;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

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
