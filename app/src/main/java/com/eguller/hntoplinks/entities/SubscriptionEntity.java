package com.eguller.hntoplinks.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.function.Function;

@Data
@Table("subscription")
@EqualsAndHashCode(of = {"id"})
public class SubscriptionEntity implements HnEntity {
  @Id
  @Column("id")
  private Long      id;

  @Column("subscriber_id")
  private SubscriberEntity subscriber;

  @Column("period")
  private Period period;

  @Column("next_send_date")
  LocalDateTime nextSendDate;


  private static Function<LocalDateTime, Boolean> isSubscriptionExpired = localDateTime -> {
    var expired = (localDateTime == null || LocalDateTime.now().isAfter(localDateTime));
    return expired;
  };


  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

}

