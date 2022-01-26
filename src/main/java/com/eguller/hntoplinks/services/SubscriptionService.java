package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class SubscriptionService {
  private static final Logger                 logger         = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final int                    EXPIRE_IN_DAYS = 7;
  @Autowired
  private              SubscriptionRepository subscriptionRepository;

  public void deleteExpiredInactiveSubscriptions() {
    LocalDate expiryDate = LocalDate.now().minus(EXPIRE_IN_DAYS, ChronoUnit.DAYS);
    int deleted = subscriptionRepository.deleteBySubscriptionDateBeforeAndActivatedIsFalse(expiryDate);
    if (deleted > 0) {
      logger.info("{} non-activated subscription was deleted.", deleted);
    }
  }
}
