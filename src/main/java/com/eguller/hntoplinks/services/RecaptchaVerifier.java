package com.eguller.hntoplinks.services;

public interface RecaptchaVerifier {
  boolean verify(String recaptchaResponse);
}
