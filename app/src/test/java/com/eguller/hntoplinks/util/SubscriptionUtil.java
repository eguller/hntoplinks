package com.eguller.hntoplinks.util;

import com.eguller.hntoplinks.controllers.ApplicationController;
import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.models.SubscriptionForm;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.util.Set;

public class SubscriptionUtil {
  public static Model subscribeDailyNew(ApplicationController applicationController, String emailAddress) {
    return subscribe(applicationController, emailAddress, null, Period.DAILY);
  }

  public static ExtendedModelMap subscribe(ApplicationController applicationController, String emailAddress, String subscriptionUuid, Period ... periods) {
    var subscriptionForm = SubscriptionForm.builder()
      .subsUUID(subscriptionUuid)
      .email(emailAddress)
      .selectedPeriods(Set.of(periods))
      .timeZone(ZoneId.of("UTC").toString())
      .build();

    var model = new ExtendedModelMap();
    applicationController.subscribe_Post(subscriptionForm, model);
    return model;
  }
}
