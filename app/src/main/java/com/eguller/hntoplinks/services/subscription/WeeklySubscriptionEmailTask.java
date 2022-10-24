package com.eguller.hntoplinks.services.subscription;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.SubscriptionService;
import com.eguller.hntoplinks.services.TemplateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeeklySubscriptionEmailTask extends SubscriptionEmailTask {
  private final StoryCacheService storyCacheService;
  public WeeklySubscriptionEmailTask(TemplateService templateService, SubscriptionEntity subscription, EmailProviderService emailProviderService, StoryCacheService storyCacheService) {
    super(templateService, subscription, emailProviderService);
    this.storyCacheService = storyCacheService;
  }

  @Override
    protected String getSubject() {
      String toDate = DateTimeFormatter.ofPattern("dd MMMM").format(LocalDateTime.now().minusDays(1).atZone(subscription.getSubscriber().getTimeZoneObj()));
      String fromDate = DateTimeFormatter.ofPattern("dd MMMM").format(LocalDateTime.now().minusDays(7).atZone(subscription.getSubscriber().getTimeZoneObj()));
      return String.format("%s - %s Weekly Top Links", fromDate, toDate);
    }

    @Override
    protected List<StoryEntity> getStories() {
      var stories = this.storyCacheService.getWeeklTop();
      return stories;
    }
  }
