package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends CrudRepository<SubscriberEntity, Long> {
  Optional<SubscriberEntity> findBySubsUUID(String subscriptionId);

  Optional<SubscriberEntity> findByEmail(String email);

  long deleteBySubscriptionDateBeforeAndActivatedIsFalse(LocalDate expiryDate);

  long deleteBySubsUUID(String subscriptionId);

    @Query("""
     select * from
     subscriber where
     subscriber.activated = true
     and
     id in
     (select distinct subscriber_id from subscription where next_send_date < now())
    """)
  List<SubscriberEntity> findSubscriptionsByExpiredNextSendDate();
}
