package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
  public static final Subscription NEW = builder().build();
  private String subsUUID;
  private String email;
  private boolean daily;
  private LocalDateTime nextSendDay;
  private boolean weekly;
  private LocalDateTime nextSendWeek;
  private boolean monthly;
  private LocalDateTime nextSendMonth;
  private boolean annually;
  private LocalDateTime nextSendYear;

  @Builder.Default
  private ZoneId        timeZone = ZoneId.systemDefault();

  public static Subscription entityToModel(SubscriptionEntity entity){
    var subscription = builder()
      .email(entity.getEmail())
      .subsUUID(entity.getSubsUUID())
      .daily(entity.isDaily())
      .nextSendDay(entity.getNextSendDay())
      .weekly(entity.isWeekly())
      .nextSendWeek(entity.getNextSendWeek())
      .monthly(entity.isMonthly())
      .nextSendMonth(entity.getNextSendMonth())
      .annually(entity.isAnnually())
      .nextSendYear(entity.getNextSendYear())
      .timeZone(DateUtils.parseZoneId(entity.getTimeZone()))
      .build();
    return subscription;
  }

  public SubscriptionEntity toEntity() {
    return toEntity(this);
  }

  public static SubscriptionEntity toEntity(Subscription subscription){
    var entity = new SubscriptionEntity();
    entity.setSubsUUID(subscription.getSubsUUID());
    entity.setEmail(subscription.getEmail().toLowerCase());
    entity.setDaily(subscription.isDaily());
    entity.setWeekly(subscription.isWeekly());
    entity.setMonthly(subscription.isMonthly());
    entity.setAnnually(subscription.isAnnually());
    entity.setTimeZone(subscription.getTimeZone().toString());
    return entity;
  }

  public boolean hasSubscription(){
    return (daily || weekly || monthly || annually);
  }

}
