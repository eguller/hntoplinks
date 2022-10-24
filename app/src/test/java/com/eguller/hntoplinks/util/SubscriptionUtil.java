package com.eguller.hntoplinks.util;

import com.eguller.hntoplinks.controllers.ApplicationController;
import com.eguller.hntoplinks.models.SubscriptionPage;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.ZoneId;

public class SubscriptionUtil {
    public static Model subscribeDailyNew(ApplicationController applicationController, String emailAddress) {
    return subscribe(applicationController, emailAddress, null, true, false, false, false);
  }

  public static ExtendedModelMap subscribe(ApplicationController applicationController, String emailAddress, String subscriptionUuid, boolean daily, boolean weekly, boolean monthly, boolean annually) {
    var subscriptionForm = SubscriptionPage.SubscriptionForm.builder()
      .subsUUID(subscriptionUuid)
      .email(emailAddress)
      .daily(daily)
      .weekly(weekly)
      .monthly(monthly)
      .yearly(annually)
      .timeZone(ZoneId.of("UTC").toString())
      .build();

    var model = new ExtendedModelMap();
    applicationController.subscribe_Post(subscriptionForm, model);
    return model;
  }
}
