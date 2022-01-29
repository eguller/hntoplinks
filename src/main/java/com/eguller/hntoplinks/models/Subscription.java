package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.SubscriptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
  public static final Subscription NEW = builder().build();
  private String id;
  private String email;
  private boolean daily;
  private boolean weekly;
  private boolean monthly;
  private boolean annually;

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
