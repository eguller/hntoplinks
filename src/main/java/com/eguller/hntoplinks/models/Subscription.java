package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.SubscriptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
  public static final Subscription NEW = builder().build();
  private Long id;
  private String subsUUID;
  private String email;
  private boolean daily;
  private boolean weekly;
  private boolean monthly;
  private boolean annually;
  private String timeZone;

  public static Subscription entityToModel(SubscriptionEntity entity){
    var subscription = builder()
      .email(entity.getEmail())
      .id(entity.getId())
      .subsUUID(entity.getSubsUUID())
      .daily(entity.isDaily())
      .weekly(entity.isWeekly())
      .monthly(entity.isMonthly())
      .annually(entity.isAnnually())

      .build();
    return subscription;
  }

  public SubscriptionEntity toEntity() {
    return toEntity(this);
  }

  public static SubscriptionEntity toEntity(Subscription subscription){
    var entity = new SubscriptionEntity();
    entity.setId(subscription.getId());
    entity.setSubsUUID(subscription.getSubsUUID());
    entity.setEmail(subscription.getEmail().toLowerCase());
    entity.setDaily(subscription.isDaily());
    entity.setWeekly(subscription.isWeekly());
    entity.setMonthly(subscription.isMonthly());
    entity.setAnnually(subscription.isAnnually());
    return entity;
  }

}
