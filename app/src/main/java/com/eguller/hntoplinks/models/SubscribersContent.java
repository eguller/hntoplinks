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
  @Builder.Default
  private boolean daily = false;
  @Builder.Default
  private boolean weekly = false;
  @Builder.Default
  private boolean monthly = false;
  @Builder.Default
  private boolean yearly = false;

  @Builder.Default
  private boolean success = false;
  @Builder.Default
  private boolean confirmed = false;
  @Builder.Default
  private boolean error = false;
}
