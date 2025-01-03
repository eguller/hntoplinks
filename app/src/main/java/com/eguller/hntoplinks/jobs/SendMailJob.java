package com.eguller.hntoplinks.jobs;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.SubscribersRepository;
import com.eguller.hntoplinks.services.SubscriptionService;

@Component
public class SendMailJob {
  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final SubscribersRepository subscriberRepository;
  private final SubscriptionService subscriptionService;

  public SendMailJob(
      SubscribersRepository subscriberRepository, SubscriptionService subscriptionService) {
    this.subscriberRepository = subscriberRepository;
    this.subscriptionService = subscriptionService;
  }

  @Scheduled(cron = "${hntoplinks.send-stories-email.cron}")
  public void sendEmail() {
    var subscribersToSendEmail = subscriberRepository.findSubscriptionsByExpiredNextSendDate();
    subscribersToSendEmail.stream()
        .flatMap(
            subscriber ->
                subscriber.getSubscriptionList().stream()
                    .map(subscriptionEntity -> new EmailTarget(subscriber, subscriptionEntity)))
        .forEach(emailTarget -> subscriptionService.sendSubscriptionEmail(emailTarget));
  }
}
