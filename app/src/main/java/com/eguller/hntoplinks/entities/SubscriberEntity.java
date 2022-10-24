package com.eguller.hntoplinks.entities;

import com.eguller.hntoplinks.util.DateUtils;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Data
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
  private List<SubscriptionEntity> subscriptionList;


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
}
