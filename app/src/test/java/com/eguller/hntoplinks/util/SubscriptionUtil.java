package com.eguller.hntoplinks.util;

import java.time.ZoneId;
import java.util.Set;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.eguller.hntoplinks.controllers.StoriesController;
import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.models.SubscriptionForm;

public class SubscriptionUtil {
  public static Model subscribeDailyNew(
      StoriesController applicationController, String emailAddress) {
    return subscribe(applicationController, emailAddress, null, Period.DAILY);
  }

  public static ExtendedModelMap subscribe(
      StoriesController applicationController,
      String emailAddress,
      String subscriptionUuid,
      Period... periods) {
    var subscriptionForm =
        SubscriptionForm.builder()
            .subscriberId(subscriptionUuid)
            .email(emailAddress)
            .selectedPeriods(Set.of(periods))
            .timeZone(ZoneId.of("UTC").toString())
            .build();

    var model = new ExtendedModelMap();
    applicationController.subscribe_Post(subscriptionForm, model);
    return model;
  }
}
