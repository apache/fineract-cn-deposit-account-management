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
package org.apache.fineract.cn.deposit.service.rest;

import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.BeatListenerCommand;
import javax.validation.Valid;
import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.rhythm.spi.v1.client.BeatListener;
import org.apache.fineract.cn.rhythm.spi.v1.domain.BeatPublish;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BeatListener.PUBLISH_BEAT_PATH)
public class BeatListenerRestController {

  private final static String BEAT_PUBLISH_PERMISSION = "deposit__v1__khepri";

  private final Logger logger;
  private final CommandGateway commandGateway;

  @Autowired
  public BeatListenerRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                    final CommandGateway commandGateway) {
    super();
    this.logger = logger;
    this.commandGateway = commandGateway;
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = BEAT_PUBLISH_PERMISSION)
  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public @ResponseBody
  ResponseEntity<Void> publishBeat(@RequestBody @Valid final BeatPublish beatPublish)
  {
    this.commandGateway.process(new BeatListenerCommand(beatPublish));
    return ResponseEntity.accepted().build();
  }
}
