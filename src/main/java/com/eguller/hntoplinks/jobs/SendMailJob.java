package com.eguller.hntoplinks.jobs;

import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component

public class SendMailJob {

  @Autowired
  private SubscriptionRepository subscriptionRepository;

  @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
  public void sendEmail(){
    var subscriptionsToSendEmail = subscriptionRepository.findSubscriptionToSendEmail();
    //var dailyEmailsList = subscriptionsToSendEmail.stream().filter(subscription -> subscription.isDaily());
    //var weeklyEmailsList = subscriptionsToSendEmail.stream().filter(subscription -> subscription.isWeekly());
    //var monthlyEmailList = subscriptionsToSendEmail.stream().filter(subscription -> subscription.isAnnually());
  }

  private SubscriptionEntity sendEmail(SubscriptionEntity subscription){
    if(subscription.isDaily()){

    }
    return null;
  }
}
