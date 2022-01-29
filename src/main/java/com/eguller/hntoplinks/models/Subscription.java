package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.SubscriptionEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Subscription {
  public static final Subscription NEW = builder().build();
  private final String id;
  private final String email;
  private final boolean daily;
  private final boolean weekly;
  private final boolean monthly;
  private final boolean annually;

  public static Subscription entityToModel(SubscriptionEntity entity){
    var subscription = builder()
      .daily(entity.isDaily())
      .weekly(entity.isWeekly())
      .monthly(entity.isMonthly())
      .annually(entity.isAnnually())
      .build();
    return subscription;
  }
}
