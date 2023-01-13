package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.Period;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Data
public class SubscriptionForm {
  private String subsUUID;

  @Builder.Default
  private String email = "";

  private String gRecaptchaResponse;

  @Builder.Default
  private String timeZone = "UTC";

  @Builder.Default
  private Set<Period> selectedPeriods = new HashSet<>();

}
