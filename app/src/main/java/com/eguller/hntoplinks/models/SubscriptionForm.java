package com.eguller.hntoplinks.models;

import java.util.HashSet;
import java.util.Set;

import com.eguller.hntoplinks.entities.Period;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscriptionForm {
  private String subscriberId;

  @Builder.Default private String email = "";

  private String gRecaptchaResponse;

  @Builder.Default private String timeZone = "UTC";

  @Builder.Default private Set<Period> selectedPeriods = new HashSet<>();
}
