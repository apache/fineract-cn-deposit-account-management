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
package io.mifos.deposit.service.internal.service;

import io.mifos.deposit.api.v1.definition.domain.Action;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.deposit.service.internal.mapper.ActionMapper;
import io.mifos.deposit.service.internal.repository.ActionRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActionService {

  private final Logger logger;
  private final ActionRepository actionRepository;

  @Autowired
  public ActionService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                       final ActionRepository actionRepository) {
    super();
    this.logger = logger;
    this.actionRepository = actionRepository;
  }

  public Optional<Action> findByIdentifier(final String identifier) {
    return this.actionRepository.findByIdentifier(identifier).map(ActionMapper::map);
  }

  public List<Action> fetchActions() {
    return this.actionRepository.findAll().stream().map(ActionMapper::map).collect(Collectors.toList());
  }
}
