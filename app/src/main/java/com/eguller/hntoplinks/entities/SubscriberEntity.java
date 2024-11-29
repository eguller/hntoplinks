package com.eguller.hntoplinks.entities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import com.eguller.hntoplinks.util.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"subscriptionList"})
@Table("subscribers")
public class SubscriberEntity implements HnEntity {
  @Id
  @Column("id")
  private Long id;

  @Column("email")
  private String email;

  @Column("subscriber_id")
  private String subscriberId;

  @Column("subscription_date")
  private LocalDateTime subscriptionDate;

  @Column("activation_date")
  private LocalDateTime activationDate;

  @Column("timezone")
  @Builder.Default
  private String timeZone = ZoneId.systemDefault().getId();

  @Column("activated")
  @Builder.Default
  private boolean activated = true;

  @MappedCollection(idColumn = "subscriber_id", keyColumn = "id")
  @Builder.Default
  private List<SubscriptionEntity> subscriptionList = new ArrayList<>();

  public SubscriptionEntity createNewSubscription(Period period) {
    SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
    subscriptionEntity.setPeriod(period);
    var nextSendDate =
        switch (period) {
          case DAILY -> DateUtils.tomorrow_7_AM(DateUtils.zoneOf(timeZone));
          case WEEKLY -> DateUtils.nextMonday_7_AM(DateUtils.zoneOf(timeZone));
          case MONTHLY -> DateUtils.firstDayOfNextMonth_7_AM(DateUtils.zoneOf(timeZone));
          case YEARLY -> DateUtils.firstDayOfNextYear_7_AM(DateUtils.zoneOf(timeZone));
        };
    subscriptionEntity.setNextSendDate(nextSendDate);
    return subscriptionEntity;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public boolean isSubscribedFor(Period period) {
    return subscriptionList.stream().anyMatch(subscription -> subscription.getPeriod() == period);
  }

  public ZoneId getTimeZoneObj() {
    var timeZoneObj = DateUtils.zoneOf(timeZone);
    return timeZoneObj;
  }

  public boolean hasSubscription(Period period) {
    var exist =
        subscriptionList.stream()
            .map(subscription -> subscription.getPeriod())
            .anyMatch(p -> p == period);
    return exist;
  }

  public boolean isNew() {
    return id == null;
  }
}
