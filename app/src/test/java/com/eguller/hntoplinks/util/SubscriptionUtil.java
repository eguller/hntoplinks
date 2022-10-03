package com.eguller.hntoplinks.util;

import com.eguller.hntoplinks.controllers.ApplicationController;
import com.eguller.hntoplinks.models.Subscription;
import com.eguller.hntoplinks.models.SubscriptionForm;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.util.TimeZone;

public class SubscriptionUtil {
    public static Model subscribeDailyNew(ApplicationController applicationController, String emailAddress) {
    return subscribe(applicationController, emailAddress, null, true, false, false, false);
  }

  public static ExtendedModelMap subscribe(ApplicationController applicationController, String emailAddress, String subscriptionUuid, boolean daily, boolean weekly, boolean monthly, boolean annually) {
    var subscription = Subscription.builder()
      .subsUUID(subscriptionUuid)
      .email(emailAddress)
      .daily(daily)
      .weekly(weekly)
      .monthly(monthly)
      .annually(annually)
      .timeZone(ZoneId.of("UTC"))
      .build();
    var subscriptionForm = SubscriptionForm.builder().subscription(subscription).build();

    var model = new ExtendedModelMap();
    applicationController.subscribe_Post(subscriptionForm, null, model);
    return model;
  }
}
