package com.eguller.hntoplinks.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.eguller.hntoplinks.entities.SubscriptionEntity;

public interface SubscriptionsRepository extends CrudRepository<SubscriptionEntity, Long> {

  @Modifying
  @Transactional
  @Query(
      """
     DELETE
     FROM
        subscriptions
     WHERE
        subscriber_id = :subscriberId
    """)
  void deleteBySubscriberId(Long subscriberId);
}
