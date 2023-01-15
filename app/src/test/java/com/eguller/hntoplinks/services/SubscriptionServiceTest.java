package com.eguller.hntoplinks.services;


import com.eguller.hntoplinks.Application;
import com.eguller.hntoplinks.controllers.ApplicationController;
import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.models.SubscriptionPage;
import com.eguller.hntoplinks.repository.SubscriberRepository;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import com.eguller.hntoplinks.util.SubscriptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;

@SpringBootTest(classes = {Application.class})
@ActiveProfiles({"local", "test"})
public class SubscriptionServiceTest {
  @Autowired
  private ApplicationController applicationController;

  @Autowired
  private SubscriberRepository subscriberRepository;

  @Autowired
  private MockEmailStore mockEmailStore;

  @Test
  public void test_subscribe() {
    var emailAddress = "test_subscribe1@hntoplinks.com";
    SubscriptionUtil.subscribeDailyNew(this.applicationController, emailAddress);

    var subscriptionEntity = subscriberRepository.findByEmail(emailAddress);
    Assertions.assertEquals(emailAddress, subscriptionEntity.get().getEmail());
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.DAILY));
    Assertions.assertFalse(subscriptionEntity.get().isSubscribedFor(Period.WEEKLY));
    Assertions.assertFalse(subscriptionEntity.get().isSubscribedFor(Period.MONTHLY));
    Assertions.assertFalse(subscriptionEntity.get().isSubscribedFor(Period.YEARLY));

    Email email = mockEmailStore.getLastMail(emailAddress).orElseThrow();
    Assertions.assertTrue(email.getTo().contains(emailAddress));
    Assertions.assertEquals("[hntoplinks] - Welcome to hntoplinks.com", email.getSubject());
  }

  @Test
  public void test_unsubscribe() {
    var emailAddress = "test_unsubscribe1@hntoplinks.com";
    var model = SubscriptionUtil.subscribeDailyNew(this.applicationController, emailAddress);
    var subscriptionEntity = subscriberRepository.findByEmail(emailAddress);
    Assertions.assertEquals(emailAddress, subscriptionEntity.get().getEmail());
    applicationController.unsubscribe_Get(new ExtendedModelMap(), subscriptionEntity.get().getSubsUUID());
    subscriptionEntity = subscriberRepository.findByEmail(emailAddress);
    Assertions.assertFalse(subscriptionEntity.isPresent());
  }

  @Test
  public void test_updateSubscription() {
    var emailAddress = "test_updatesubscription1@hntoplinks.com";
    var model = SubscriptionUtil.subscribeDailyNew(this.applicationController, emailAddress);
    var subscriptionEntity = subscriberRepository.findByEmail(emailAddress);
    Assertions.assertEquals(emailAddress, subscriptionEntity.get().getEmail());
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.DAILY));
    Assertions.assertFalse(subscriptionEntity.get().isSubscribedFor(Period.WEEKLY));

    SubscriptionUtil.subscribe(this.applicationController, emailAddress, ((SubscriptionPage) model.getAttribute("page")).getSubscriptionForm().getSubsUUID(), Period.DAILY, Period.WEEKLY);
    subscriptionEntity = subscriberRepository.findByEmail(emailAddress);
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.DAILY));
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.WEEKLY));

    SubscriptionUtil.subscribe(this.applicationController, emailAddress, ((SubscriptionPage) model.getAttribute("page")).getSubscriptionForm().getSubsUUID(), Period.DAILY, Period.WEEKLY, Period.MONTHLY, Period.YEARLY);
    subscriptionEntity = subscriberRepository.findByEmail(emailAddress);
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.DAILY));
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.WEEKLY));
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.MONTHLY));
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.YEARLY));

    SubscriptionUtil.subscribe(this.applicationController, emailAddress, ((SubscriptionPage) model.getAttribute("page")).getSubscriptionForm().getSubsUUID(), Period.DAILY, Period.WEEKLY, Period.MONTHLY);
    subscriptionEntity = subscriberRepository.findByEmail(emailAddress);
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.DAILY));
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.WEEKLY));
    Assertions.assertTrue(subscriptionEntity.get().isSubscribedFor(Period.MONTHLY));
    Assertions.assertFalse(subscriptionEntity.get().isSubscribedFor(Period.YEARLY));

  }


}
