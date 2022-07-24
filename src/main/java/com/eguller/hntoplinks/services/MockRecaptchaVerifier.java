package com.eguller.hntoplinks.services;

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
