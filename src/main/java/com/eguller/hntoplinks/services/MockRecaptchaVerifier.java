package com.eguller.hntoplinks.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value="hntoplinks.captcha.enabled", havingValue = "false", matchIfMissing = false)
public class MockRecaptchaVerifier implements RecaptchaVerifier {
  @Override
  public boolean verify(String recaptchaResponse) {
    if("false".equalsIgnoreCase(recaptchaResponse)){
      return false;
    } else {
      return true;
    }
  }
}
