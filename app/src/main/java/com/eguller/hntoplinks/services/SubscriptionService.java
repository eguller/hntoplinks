package com.eguller.hntoplinks.services;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.ItemsRepository;
import com.eguller.hntoplinks.repository.SubscriptionsRepository;
import com.eguller.hntoplinks.services.subscription.DailySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.MonthlySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.SubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.WeeklySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.YearlySubscriptionEmailTask;

@Service
public class SubscriptionService {
  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ItemsRepository itemsRepository;
  private final EmailProviderService emailProviderService;
  private final TemplateService templateService;
  private final SubscriptionsRepository subscriptionRepository;

  public SubscriptionService(
      ItemsRepository itemsRepository,
      EmailProviderService emailProviderService,
      TemplateService templateService,
      SubscriptionsRepository subscriberRepository) {
    this.itemsRepository = itemsRepository;
    this.emailProviderService = emailProviderService;
    this.templateService = templateService;
    this.subscriptionRepository = subscriberRepository;
  }

  public void sendSubscriptionEmail(EmailTarget emailTarget) {
    // do not send email if it is not yet expired.
    if (!emailTarget.subscription().isExpired()) {
      return;
    }

    try {
      var task = createTask(emailTarget);
      task.execute();
      this.subscriptionRepository.save(emailTarget.subscription());
    } catch (Exception ex) {
      logger.error(
          "Sending email has failed. subscriberId=%s, period=%s"
              .formatted(
                  emailTarget.subscriber().getSubscriberId(),
                  emailTarget.subscription().getPeriod()),
          ex);
    }
  }

  private SubscriptionEmailTask createTask(EmailTarget emailTarget) {
    var task =
        switch (emailTarget.subscription().getPeriod()) {
          case WEEKLY -> new WeeklySubscriptionEmailTask(
              templateService, emailTarget, emailProviderService, itemsRepository);
          case MONTHLY -> new MonthlySubscriptionEmailTask(
              templateService, emailTarget, emailProviderService, itemsRepository);
          case YEARLY -> new YearlySubscriptionEmailTask(
              templateService, emailTarget, emailProviderService, itemsRepository);
          default -> new DailySubscriptionEmailTask(
              templateService, emailTarget, emailProviderService, itemsRepository);
        };

    return task;
  }
}
