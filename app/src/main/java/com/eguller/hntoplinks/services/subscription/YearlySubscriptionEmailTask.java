package com.eguller.hntoplinks.services.subscription;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.TemplateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class YearlySubscriptionEmailTask extends SubscriptionEmailTask {
  private final StoryCacheService storyCacheService;

  public YearlySubscriptionEmailTask(TemplateService templateService, SubscriberEntity subscriber, SubscriptionEntity subscription, EmailProviderService emailProviderService, StoryCacheService storyCacheService) {
    super(templateService, subscriber, subscription, emailProviderService);
    this.storyCacheService = storyCacheService;
  }

  @Override
  protected String getSubject() {
    String lastYear = DateTimeFormatter.ofPattern("YYYY").format(LocalDateTime.now().minusYears(1).atZone(subscriber.getTimeZoneObj()));
    return "Best of " + lastYear;
  }

  @Override
  protected List<StoryEntity> getStories() {
    var stories = this.storyCacheService.getYearlyTop();
    return stories;
  }
}
