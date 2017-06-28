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
package io.mifos.deposit.service.internal.mapper;

import io.mifos.core.lang.ServiceException;
import io.mifos.deposit.api.v1.definition.domain.Charge;
import io.mifos.deposit.service.internal.repository.ActionEntity;
import io.mifos.deposit.service.internal.repository.ActionRepository;
import io.mifos.deposit.service.internal.repository.ChargeEntity;

import java.util.Optional;

public class ChargeMapper {

  private ChargeMapper() {
    super();
  }

  public static ChargeEntity map(final Charge charge, final ActionRepository actionRepository) {
    final Optional<ActionEntity> optionalActionEntity = actionRepository.findByIdentifier(charge.getActionIdentifier());
    if (optionalActionEntity.isPresent()) {
      final ChargeEntity chargeEntity = new ChargeEntity();
      chargeEntity.setActionId(optionalActionEntity.get().getId());
      chargeEntity.setIncomeAccountIdentifier(charge.getIncomeAccountIdentifier());
      chargeEntity.setName(charge.getName());
      chargeEntity.setDescription(charge.getDescription());
      chargeEntity.setProportional(charge.getProportional());
      chargeEntity.setAmount(charge.getAmount());

      return chargeEntity;
    } else {
      throw ServiceException.notFound("Can not create charge, action {1} not found.",
          charge.getActionIdentifier());
    }
  }

  public static Charge map(final ChargeEntity chargeEntity, final ActionRepository actionRepository) {
    final Charge charge = new Charge();
    final ActionEntity actionEntity = actionRepository.findOne(chargeEntity.getActionId());
    charge.setActionIdentifier(actionEntity.getIdentifier());
    charge.setIncomeAccountIdentifier(chargeEntity.getIncomeAccountIdentifier());
    charge.setName(chargeEntity.getName());
    charge.setDescription(chargeEntity.getDescription());
    charge.setProportional(chargeEntity.getProportional());
    charge.setAmount(chargeEntity.getAmount());

    return charge;
  }
}
