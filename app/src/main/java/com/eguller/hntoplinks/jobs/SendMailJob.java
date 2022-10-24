package com.eguller.hntoplinks.jobs;

import com.eguller.hntoplinks.repository.SubscriptionRepository;
import com.eguller.hntoplinks.services.EmailService;
import com.eguller.hntoplinks.services.StatisticsService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.SubscriptionService;
import com.eguller.hntoplinks.services.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component

public class SendMailJob {
  private static final Logger                 logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  @Autowired
  private              SubscriptionRepository subscriptionRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private StoryCacheService storyCacheService;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private StatisticsService statisticsService;

  @Autowired
  SubscriptionService subscriptionService;

  @Scheduled(initialDelay = 5, fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
  public void sendEmail() {
    var subscriptionsToSendEmail = subscriptionRepository.findSubscriptionsByExpiredNextSendDate();
    var updatedSubscriptions = subscriptionsToSendEmail.stream()
      .map(subscription -> subscriptionService.sendSubscriptionEmail(subscription))
      .collect(Collectors.toSet());
    subscriptionRepository.saveAll(updatedSubscriptions);
  }

  /*
  private void sendEmail(SubscriptionEntity subscription) {
    var subscriptionModel = Subscription.entityToModel(subscription);
    if (subscription.isDaily()) {
      if (subscription.isDailyExpired()) {
        var topEmail = DailyTopLinksEmail.builder()
          .templateService(templateService)
          .storyCacheService(storyCacheService)
          .subscription(subscriptionModel).build();
        sendEmail(topEmail)
          .onSuccess(() -> subscription.setNextSendDay(DateUtils.tomorrow_7_AM(subscriptionModel.getTimeZone())))
          .execute();

      }
    } else if (subscription.isWeekly()) {
      if (subscription.isWeeklyExpired()) {
        var topEmail = WeeklyTopLinksEmail.builder()
          .templateService(templateService)
          .storyCacheService(storyCacheService)
          .subscription(Subscription.entityToModel(subscription)).build();

        sendEmail(topEmail)
          .onSuccess(() -> subscription.setNextSendWeek(DateUtils.nextMonday_7_AM(subscriptionModel.getTimeZone())))
          .execute();

      }
    } else if (subscription.isMonthly()) {
      if (subscription.isMonthlyExpired()) {
        var topEmail = MonthlyTopLinksEmail.builder()
          .templateService(templateService)
          .storyCacheService(storyCacheService)
          .subscription(Subscription.entityToModel(subscription)).build();
        sendEmail(topEmail)
          .onSuccess(() -> subscription.setNextSendMonth(DateUtils.firstDayOfNextMonth_7_AM(subscriptionModel.getTimeZone())))
          .execute();

      }
    } else if (subscription.isAnnually()) {
      if (subscription.isAnnuallyExpired()) {
        var topEmail = AnnuallyTopLinksEmail.builder()
          .templateService(templateService)
          .storyCacheService(storyCacheService)
          .subscription(Subscription.entityToModel(subscription)).build();
        sendEmail(topEmail)
          .onSuccess(() -> subscription.setNextSendYear(DateUtils.firstDayOfNextYear_7_AM(subscriptionModel.getTimeZone())))
          .execute();
      }
    } else {
      logger.warn("Subscription does not have any email configuration.");
    }
  }

  private SendEmailTask sendEmail(TopLinksEmail topLinksEmail) {
    return new SendEmailTask(emailService, statisticsService, topLinksEmail);
  }

  private static class SendEmailTask {

    private final EmailService  emailService;
    private final TopLinksEmail topLinksEmail;

    private final StatisticsService statisticsService;
    private       Runnable          onSuccess;
    private       Runnable          onFail;

    public SendEmailTask(EmailService emailService, StatisticsService statisticsService, TopLinksEmail topLinksEmail) {
      this.emailService      = emailService;
      this.statisticsService = statisticsService;
      this.topLinksEmail     = topLinksEmail;
    }

    public void execute() {
      var result = emailService.sendTopLinksEmail(topLinksEmail);
      if (result.getFailed().isEmpty()) {
        statisticsService.sendEmailSuccess();
        if (onSuccess != null) {
          onSuccess.run();
        }
      } else {
        statisticsService.sendEmailFailed();
        if (onFail != null) {
          onFail.run();
        }
      }
    }

    public SendEmailTask onSuccess(Runnable r) {
      this.onSuccess = r;
      return this;
    }

    public SendEmailTask onFailure(Runnable r) {
      this.onFail = r;
      return this;
    }
  }
  */
}
