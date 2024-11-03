package com.eguller.hntoplinks.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.models.Email;

@Service
public class EmailService {
  @Autowired private EmailProviderService emailProviderService;
  @Autowired private TemplateService templateService;

  public void sendSubscriptionEmail(SubscriberEntity subscriber) {
    var content = templateService.generateSubscriptionEmail(subscriber);
    var email =
        Email.builder()
            .to(subscriber.getEmail())
            .subject("[hntoplinks] - Welcome to hntoplinks.com")
            .html(content)
            .build();
    emailProviderService.send(email);
  }
}
