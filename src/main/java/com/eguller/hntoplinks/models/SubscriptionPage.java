package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SubscriptionPage extends Page {
  @Builder.Default
  private       PageTab      activeTab = PageTab.subscribe;
  private final Subscription subscription;

  private boolean captchaEnabled;
  @Singular
  private final List<String> errors;

  @Singular
  private final List<String> messages;

  public boolean hasErrors() {
    return !CollectionUtils.isEmpty(errors);
  }

  public boolean hasMessages() {
    return !CollectionUtils.isEmpty(messages);
  }
}
