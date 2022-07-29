package com.eguller.hntoplinks.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value="hntoplinks.captcha.enabled", havingValue = "true", matchIfMissing = true)
public class RecaptchaVerifierImpl implements RecaptchaVerifier{
  @Override
  public boolean verify(String recaptchaResponse) {
    return true;
  }
}
