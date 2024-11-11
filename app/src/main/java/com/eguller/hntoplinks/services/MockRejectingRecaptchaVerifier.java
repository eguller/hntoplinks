package com.eguller.hntoplinks.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "hntoplinks.captcha.impl", havingValue = "mock-reject")
public class MockRejectingRecaptchaVerifier implements RecaptchaVerifier {
  public boolean verify(String recaptchaResponse) {
    return false;
  }
}
