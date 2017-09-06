package io.mifos.deposit.service.internal.service.helper;

import io.mifos.core.api.util.NotFoundException;
import io.mifos.core.lang.ApplicationName;
import io.mifos.deposit.service.ServiceConstants;
import io.mifos.rhythm.api.v1.client.RhythmManager;
import io.mifos.rhythm.api.v1.domain.Beat;
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
