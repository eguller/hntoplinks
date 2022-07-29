package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Email;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "hntoplinks.email-provider", havingValue = "mandrill", matchIfMissing = false)
public class MandrillEmailProviderService implements EmailProviderService {
  @Override
  public void send(Email mail) {

  }

  @Override
  public void sendAsync(Email email) {

  }
}
