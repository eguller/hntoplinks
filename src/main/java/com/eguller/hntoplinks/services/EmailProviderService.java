package com.eguller.hntoplinks.services;


import com.eguller.hntoplinks.models.Email;
import org.springframework.scheduling.annotation.Async;

public interface EmailProviderService {
  void send(Email mail);

  @Async
  void sendAsync(Email email);
}
