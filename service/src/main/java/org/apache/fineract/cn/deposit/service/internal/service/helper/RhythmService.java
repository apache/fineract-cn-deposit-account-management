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
package org.apache.fineract.cn.deposit.service.internal.service.helper;

import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.api.util.NotFoundException;
import org.apache.fineract.cn.lang.ApplicationName;
import org.apache.fineract.cn.rhythm.api.v1.client.RhythmManager;
import org.apache.fineract.cn.rhythm.api.v1.domain.Beat;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RhythmService {

  private final Logger logger;
  private final RhythmManager rhythmManager;
  private final ApplicationName applicationName;

  @Autowired
  public RhythmService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                       final RhythmManager rhythmManager,
                       final ApplicationName applicationName) {
    super();
    this.logger = logger;
    this.rhythmManager = rhythmManager;
    this.applicationName = applicationName;
  }

  public void setBeat() {
    try {
      rhythmManager.getBeat(this.applicationName.toString(), "deposit-interest-accrual");
    } catch (final NotFoundException nfex) {
      final Beat beat = new Beat();
      beat.setIdentifier("deposit-interest-accrual");
      beat.setAlignmentHour(22);

      try {
        this.rhythmManager.createBeat(applicationName.toString(), beat);
      } catch (final Throwable th) {
        this.logger.error("Error while creating beat: ", th);
      }
    }
  }
}
