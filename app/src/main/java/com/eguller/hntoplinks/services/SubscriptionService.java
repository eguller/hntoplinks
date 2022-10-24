package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.services.subscription.DailySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.MonthlySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.SubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.WeeklySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.YearlySubscriptionEmailTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
  @Autowired
  private StoryCacheService    storyCacheService;
  @Autowired
  private EmailProviderService emailProviderService;
  @Autowired
  private TemplateService      templateService;


  public SubscriptionEntity sendSubscriptionEmail(SubscriptionEntity subscription) {
    var task = createTask(subscription);
    task.execute();
    return subscription;
  }

  private SubscriptionEmailTask createTask(SubscriptionEntity subscription) {
    SubscriptionEmailTask task = null;
    if (Period.DAILY == subscription.getPeriod()) {
      task = new DailySubscriptionEmailTask(templateService, subscription, emailProviderService, storyCacheService);
    } else if (Period.WEEKLY == subscription.getPeriod()) {
      task = new WeeklySubscriptionEmailTask(templateService, subscription, emailProviderService, storyCacheService);
    } else if (Period.MONTHLY == subscription.getPeriod()) {
      task = new MonthlySubscriptionEmailTask(templateService, subscription, emailProviderService, storyCacheService);
    } else if (Period.YEARLY == subscription.getPeriod()) {
      task = new YearlySubscriptionEmailTask(templateService, subscription, emailProviderService, storyCacheService);
    }
    return task;
  }
}
