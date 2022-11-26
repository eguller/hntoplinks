package com.eguller.hntoplinks.entities;

import com.eguller.hntoplinks.util.DateUtils;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import javax.xml.catalog.Catalog;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"subscriptionList"})
@Table("subscriber")
public class SubscriberEntity implements HnEntity {
  @Id
  @Column("id")
  private Long id;

  @Column("email")
  private String email;

  @Column("subsuuid")
  private String subsUUID;

  @Column("subscription_date")
  private LocalDate subscriptionDate;

  @Column("timezone")
  private String timeZone = ZoneId.systemDefault().getId();

  @Column("activated")
  private boolean activated = true;

  @MappedCollection(idColumn = "id", keyColumn = "subscriber_id")
  private List<SubscriptionEntity> subscriptionList = new ArrayList<>();

  public SubscriptionEntity createNewSubscription(Period period) {
    SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
    subscriptionEntity.setSubscriberId(this.id);
    subscriptionEntity.setPeriod(period);
    var nextSendDate = switch (period) {
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
    var exist = subscriptionList
      .stream()
      .map(subscription -> subscription.getPeriod())
      .anyMatch(p -> p == period);
    return exist;
  }
}
