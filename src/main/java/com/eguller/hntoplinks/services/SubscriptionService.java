package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Subscription;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import com.eguller.hntoplinks.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionService {
  private static final Logger                 logger         = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final int                    EXPIRE_IN_DAYS = 7;
  @Autowired
  private              SubscriptionRepository subscriptionRepository;

  @Autowired
  private StatisticsService statisticsService;

  public void deleteExpiredInactiveSubscriptions() {
    LocalDate expiryDate = LocalDate.now().minus(EXPIRE_IN_DAYS, ChronoUnit.DAYS);
    int deleted = subscriptionRepository.deleteBySubscriptionDateBeforeAndActivatedIsFalse(expiryDate);
    if (deleted > 0) {
      logger.info("{} non-activated subscription was deleted.", deleted);
    }
  }

  public Optional<Subscription> findBySubscriptionId(String subscriptionId) {
    var subscriptionEntity = subscriptionRepository.findBySubsUUID(subscriptionId);
    var subscriptionOpt = subscriptionEntity.map(Subscription::entityToModel);
    return subscriptionOpt;
  }

  public Optional<Subscription> findByEmail(String email) {
    var subscriptionEntity = subscriptionRepository.findByEmail(email);
    var subscriptionOpt = subscriptionEntity.map(Subscription::entityToModel);
    return subscriptionOpt;
  }

  public boolean unsubscribe(String uuid){
    var numberOfDeletedEntities = subscriptionRepository.deleteBySubsUUID(uuid);
    return numberOfDeletedEntities > 0;
  }


  public Subscription save(Subscription subscription) {

    var subscriptionEntity = Optional.ofNullable(subscription.getSubsUUID())
      .flatMap(id -> subscriptionRepository.findBySubsUUID(subscription.getSubsUUID()))
      .orElseGet(() -> {
        var newSubscriptionEntity = subscription.toEntity();
        newSubscriptionEntity.setSubsUUID(UUID.randomUUID().toString());
        newSubscriptionEntity.setActivated(true);
        newSubscriptionEntity.setActivationDate(LocalDate.now());

        newSubscriptionEntity.setSubscriptionDate(LocalDate.now());
        newSubscriptionEntity.setEmail(subscription.getEmail());
        return newSubscriptionEntity;
      });


    var zoneId = subscription.getTimeZone();

    subscriptionEntity.setTimeZone(zoneId.getId());

    subscriptionEntity.setDaily(subscription.isDaily());
    subscriptionEntity.setNextSendDay(DateUtils.tomorrow_7_AM(zoneId));

    subscriptionEntity.setWeekly(subscription.isWeekly());
    subscriptionEntity.setNextSendWeek(DateUtils.nextMonday_7_AM(zoneId));

    subscriptionEntity.setMonthly(subscription.isMonthly());
    subscriptionEntity.setNextSendMonth(DateUtils.firstDayOfNextMonth_7_AM(zoneId));

    subscriptionEntity.setAnnually(subscription.isAnnually());
    subscriptionEntity.setNextSendYear(DateUtils.firstDayOfNextYear_7_AM(zoneId));

    subscriptionEntity = subscriptionRepository.save(subscriptionEntity);

    var savedSubscription = Subscription.entityToModel(subscriptionEntity);
    return savedSubscription;
  }
}
