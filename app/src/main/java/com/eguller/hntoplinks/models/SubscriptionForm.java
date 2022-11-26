package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.Period;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
  private List<Period> selectedPeriods = new ArrayList<>();


  //public boolean hasSubscription() {
  //  return (daily || weekly || monthly || yearly);
  //}
}
