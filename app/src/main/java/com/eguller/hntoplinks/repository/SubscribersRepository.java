package com.eguller.hntoplinks.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.eguller.hntoplinks.entities.SubscriberEntity;

public interface SubscribersRepository extends CrudRepository<SubscriberEntity, Long> {
  Optional<SubscriberEntity> findBySubscriberId(String subscriberId);

  Optional<SubscriberEntity> findByEmailIgnoreCase(String email);

  long deleteBySubscriptionDateBeforeAndActivatedIsFalse(LocalDate expiryDate);

  void deleteBySubscriberId(String subscriberId);

  @Query(
      """
     SELECT
       *
     FROM
        subscribers
     WHERE
        subscribers.activated = true
     AND
        id IN
            (SELECT DISTINCT subscriber_id FROM subscriptions WHERE next_send_date < now())
    """)
  List<SubscriberEntity> findSubscriptionsByExpiredNextSendDate();
}
