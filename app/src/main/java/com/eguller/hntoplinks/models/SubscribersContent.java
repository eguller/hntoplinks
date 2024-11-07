package com.eguller.hntoplinks.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Builder
@Data
public class SubscribersContent {
  private String email;

  private SubscriptionForm subscriptionForm;

  @Builder.Default private boolean success = false;
  private String successMessage;
}
