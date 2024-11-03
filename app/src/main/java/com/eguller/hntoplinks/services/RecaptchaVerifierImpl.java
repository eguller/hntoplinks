package com.eguller.hntoplinks.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalOnProperty(
    value = "hntoplinks.captcha.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class RecaptchaVerifierImpl implements RecaptchaVerifier {
  @Value("${hntoplinks.captcha.secret}")
  private String captchaSecret;

  @Autowired private RestTemplate client;

  @Override
  public boolean verify(String recaptchaResponse) {

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    var map = new LinkedMultiValueMap<String, String>();
    map.add("secret", captchaSecret);
    map.add("response", recaptchaResponse);

    var request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
    var response =
        client.postForEntity("https://www.google.com/recaptcha/api/siteverify", request, Map.class);
    var success = response.getBody().get("success");
    return (success != null && (success instanceof Boolean) && ((boolean) success));
  }
}
