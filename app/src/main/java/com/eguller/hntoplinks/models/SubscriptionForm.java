package com.eguller.hntoplinks.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionForm {
  //g-recaptcha-response
  private String gRecaptchaResponse;
  private Subscription subscription;
}
