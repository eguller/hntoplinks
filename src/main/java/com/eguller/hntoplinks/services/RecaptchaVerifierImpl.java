package com.eguller.hntoplinks.services;

public class RecaptchaVerifierImpl implements RecaptchaVerifier{
  @Override
  public boolean verify(String recaptchaResponse) {
    return true;
  }
}
