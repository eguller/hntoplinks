package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.SubscriptionEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, Long> {
  @Modifying
  int deleteBySubscriptionDateBeforeAndActivatedIsFalse(LocalDate expiryDate);

  long countByDailyIsTrue();

  long countByWeeklyIsTrue();

  long countByMonthlyIsTrue();

  long countByAnnuallyIsTrue();

  Optional<SubscriptionEntity> findBySubsUUID(String subscriptionId);

  Optional<SubscriptionEntity> findByEmail(String email);


  @Query("""
     select * from subscription
     where
     activated = true
     and
     (
        (daily = true and next_send_day < now())
        or
        (weekly = true and next_send_week < now())
        or
        (monthly = true and next_send_month < now())
        or
        (annually = true and next_send_year < now ())
     )
    """)
  List<SubscriptionEntity> findSubscriptionsToSendEmail();

  @Modifying
  @Query("delete from subscription where subsuuid=:subscriptionId")
  long deleteBySubsUUID(String subscriptionId);
}
