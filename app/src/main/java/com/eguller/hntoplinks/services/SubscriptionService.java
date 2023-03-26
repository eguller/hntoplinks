package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.repository.SubscriberRepository;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import com.eguller.hntoplinks.services.subscription.DailySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.MonthlySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.SubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.WeeklySubscriptionEmailTask;
import com.eguller.hntoplinks.services.subscription.YearlySubscriptionEmailTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class SubscriptionService {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final StoryRepository      storyRepository;
  private final EmailProviderService emailProviderService;
  private final TemplateService        templateService;
  private final SubscriptionRepository subscriptionRepository;

  public SubscriptionService(StoryRepository storyRepository, EmailProviderService emailProviderService, TemplateService templateService, SubscriptionRepository subscriberRepository) {
    this.storyRepository      = storyRepository;
    this.emailProviderService   = emailProviderService;
    this.templateService        = templateService;
    this.subscriptionRepository = subscriberRepository;
  }


  public void sendSubscriptionEmail(EmailTarget emailTarget) {
    //do not send email if it is not yet expired.
    if(!emailTarget.subscription().isExpired()){
      return;
    }

    try {
      var task = createTask(emailTarget);
      task.execute();
      this.subscriptionRepository.save(emailTarget.subscription());
    } catch (Exception ex) {
      logger.error("Sending email has failed. subsUUID=%s, period=%s".formatted(emailTarget.subscriber().getSubsUUID(), emailTarget.subscription().getPeriod()), ex);
    }
  }

  private SubscriptionEmailTask createTask(EmailTarget emailTarget) {
    var task = switch (emailTarget.subscription().getPeriod()) {
      case WEEKLY -> new WeeklySubscriptionEmailTask(templateService, emailTarget, emailProviderService, storyRepository);
      case MONTHLY -> new MonthlySubscriptionEmailTask(templateService, emailTarget, emailProviderService, storyRepository);
      case YEARLY -> new YearlySubscriptionEmailTask(templateService, emailTarget, emailProviderService, storyRepository);
      default -> new DailySubscriptionEmailTask(templateService, emailTarget, emailProviderService, storyRepository);
    };

    return task;
  }
}
