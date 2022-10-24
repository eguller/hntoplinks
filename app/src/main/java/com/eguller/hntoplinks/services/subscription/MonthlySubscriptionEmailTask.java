package com.eguller.hntoplinks.services.subscription;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.TemplateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MonthlySubscriptionEmailTask extends SubscriptionEmailTask {
  private final StoryCacheService storyCacheService;

  public MonthlySubscriptionEmailTask(TemplateService templateService, SubscriptionEntity subscription, EmailProviderService emailProviderService, StoryCacheService storyCacheService) {
    super(templateService, subscription, emailProviderService);
    this.storyCacheService = storyCacheService;

  }

  @Override
  protected String getSubject() {
    String lmString = DateTimeFormatter.ofPattern("dd MMMM").format(LocalDateTime.now().minusMonths(1).atZone(subscription.getSubscriber().getTimeZoneObj()));
    return lmString + " - Best of Last Month";
  }

  @Override
  protected List<StoryEntity> getStories() {
    var stories = storyCacheService.getMonthlyTop();
    return stories;
  }
}
