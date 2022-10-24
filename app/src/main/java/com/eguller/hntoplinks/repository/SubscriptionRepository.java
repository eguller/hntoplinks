package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, Long> {
  @Modifying
  int deleteBySubscriptionDateBeforeAndActivatedIsFalse(LocalDate expiryDate);

  long countByDailyIsTrue();

  long countByWeeklyIsTrue();

  long countByMonthlyIsTrue();

  long countByAnnuallyIsTrue();




  @Query("""
     select * from
     SubscriptionEntity subscription
     inner join Subscriber subscriber
     on subscription.subscriber = subscriber.id
     where
     subscriber.activated = true and
     subscription.nextSendDate < now()
    """)
  List<SubscriptionEntity> findSubscriptionsByExpiredNextSendDate();

  @Modifying
  long deleteBySubsUUID(String subscriptionId);
}
