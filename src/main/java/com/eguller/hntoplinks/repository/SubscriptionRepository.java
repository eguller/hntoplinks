package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.SubscriptionEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface SubscriptionRepository  extends CrudRepository<SubscriptionEntity, Long> {
    int deleteBySubscriptionDateBeforeAndActivatedIsFalse(LocalDate expiryDate);
    long countByDailyIsTrue();
    long countByWeeklyIsTrue();
    long countByMonthlyIsTrue();
    long countByAnnuallyIsTrue();
}
