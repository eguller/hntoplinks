package com.eguller.hntoplinks.services.subscription;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.SubscriptionService;
import com.eguller.hntoplinks.services.TemplateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DailySubscriptionEmailTask extends SubscriptionEmailTask {
  private final StoryCacheService storyCacheService;

  public DailySubscriptionEmailTask(TemplateService templateService, SubscriberEntity subscriber, SubscriptionEntity subscription, EmailProviderService emailProviderService, StoryCacheService storyCacheService) {
    super(templateService, subscriber, subscription, emailProviderService);
    this.storyCacheService = storyCacheService;

  }

  @Override
  protected String getSubject() {
    String timePrefix = DateTimeFormatter.ofPattern("EEEE, dd MMMM").format(LocalDateTime.now().minusDays(1).atZone(subscriber.getTimeZoneObj()));
    return timePrefix + " - Daily Top Links";
  }

  @Override
  protected List<StoryEntity> getStories() {
    var stories = storyCacheService.getDailyTop();
    return stories;
  }
}
