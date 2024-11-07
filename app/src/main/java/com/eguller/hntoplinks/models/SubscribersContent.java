package com.eguller.hntoplinks.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Builder
@Data
@Setter(AccessLevel.NONE)
public class SubscribersContent {
  private String email;

  private SubscriptionForm subscriptionForm;

  @Builder.Default
  private boolean success = false;
  @Builder.Default
  private boolean confirmed = false;
  @Builder.Default
  private boolean error = false;
}
