package com.eguller.hntoplinks.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "hntoplinks.captcha.impl", havingValue = "mock")
public class MockRecaptchaVerifier implements RecaptchaVerifier {
  @Override
  public boolean verify(String recaptchaResponse) {
    return true;
  }
}
