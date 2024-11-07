package com.eguller.hntoplinks.entities;

import java.time.LocalDateTime;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("subscriptions")
@EqualsAndHashCode(of = {"id"})
public class SubscriptionEntity implements HnEntity {
  @Id
  @Column("id")
  private Long id;

  @Column("period")
  private Period period;

  @Column("next_send_date")
  LocalDateTime nextSendDate;

  @Column("subscriber_id")
  private Long subscriberId;

  private static Function<LocalDateTime, Boolean> isSubscriptionExpired =
      localDateTime -> {
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

  public boolean isExpired() {
    return isSubscriptionExpired.apply(nextSendDate);
  }
}
