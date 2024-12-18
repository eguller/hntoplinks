package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscribersContent {
  private String email;

  private SubscriptionForm subscriptionForm;

  @Builder.Default private boolean success = false;
  private String successMessage;

  @Builder.Default private boolean captchaEnabled = true;
}
