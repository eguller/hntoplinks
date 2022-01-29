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
  @Singular
  private final List<String> errors;

  public boolean hasErrors(){
    return !CollectionUtils.isEmpty(errors);
  }
}
