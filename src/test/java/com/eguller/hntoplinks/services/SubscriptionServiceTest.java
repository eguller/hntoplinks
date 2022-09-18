package com.eguller.hntoplinks.services;


import com.eguller.hntoplinks.Application;
import com.eguller.hntoplinks.controllers.ApplicationController;
import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.models.Subscription;
import com.eguller.hntoplinks.models.SubscriptionForm;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootTest(classes = {Application.class})
@ActiveProfiles({"local"})
public class SubscriptionServiceTest {
  @Autowired
  private ApplicationController applicationController;

  @Autowired
  private SubscriptionRepository subscriptionRepository;

  @Autowired
  private MockEmailStore mockEmailStore;

  @Test
  public void test_subscribe() {
    var emailAddress = "test_subscribe1@hntoplinks.com";
    var model = subscribe(emailAddress);

    var subscriptionEntity = subscriptionRepository.findByEmail(emailAddress);
    Assertions.assertEquals(emailAddress, subscriptionEntity.get().getEmail());
    Assertions.assertTrue(subscriptionEntity.get().isDaily());
    Assertions.assertFalse(subscriptionEntity.get().isWeekly());
    Assertions.assertFalse(subscriptionEntity.get().isMonthly());
    Assertions.assertFalse(subscriptionEntity.get().isAnnually());

    Email email = mockEmailStore.getLastMail(emailAddress).orElseThrow();
    Assertions.assertEquals(1, email.getTo().size());
    Assertions.assertTrue(email.getTo().contains(emailAddress));
    Assertions.assertEquals("[hntoplinks] - Welcome to hntoplinks.com", email.getSubject());
  }

  @Test
  public void test_unsubscribe() {
    var emailAddress = "test_unsubscribe1@hntoplinks.com";
    var model = subscribe(emailAddress);
    var subscriptionEntity = subscriptionRepository.findByEmail(emailAddress);
    Assertions.assertEquals(emailAddress, subscriptionEntity.get().getEmail());
    applicationController.unsubscribe_Get(new ExtendedModelMap(), subscriptionEntity.get().getSubsUUID());
    subscriptionEntity = subscriptionRepository.findByEmail(emailAddress);
    Assertions.assertFalse(subscriptionEntity.isPresent());
  }

  private Model subscribe(String emailAddress) {
    var subscription = Subscription.builder()
      .email(emailAddress)
      .daily(true)
      .timeZone(ZoneId.of("UTC"))
      .build();
    var subscriptionForm = SubscriptionForm.builder().subscription(subscription).build();

    var model = new ExtendedModelMap();
    applicationController.subscribe_Post(subscriptionForm, null, model, TimeZone.getTimeZone("UTC"));
    return model;
  }

}
