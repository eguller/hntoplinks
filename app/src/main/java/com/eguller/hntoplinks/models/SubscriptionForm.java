package com.eguller.hntoplinks.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.eguller.hntoplinks.entities.Period;

import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscriptionForm {
  private String subscriberId;

  @Builder.Default private String email = "";

  private String gRecaptchaResponse;

  @Builder.Default private String timeZone = "UTC";

  @NotEmpty(message = "Please select at least one subscription period")
  @Builder.Default private Set<Period> selectedPeriods = new HashSet<>();

  public SubscriberEntity toSubscriberEntity() {
    return SubscriberEntity.builder()
      .email(email)
      .subscriberId(UUID.randomUUID().toString())
      .timeZone(timeZone)
      .subscriptionDate(LocalDateTime.now())
      .activated(true)
      .activationDate(LocalDateTime.now())
      .build();
  }

  public List<SubscriptionEntity> toSubscriptionEntities() {
    return selectedPeriods.stream()
      .map(period -> toSubscriptionEntity(period))
      .toList();
  }

  private SubscriptionEntity toSubscriptionEntity(Period period) {
    return SubscriptionEntity
      .builder()
      .period(period)
      .nextSendDate(period.nextSendDate(timeZone))
      .build();
  }
}
