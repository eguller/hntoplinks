package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.models.Subscription;
import com.eguller.hntoplinks.models.TopLinksEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Autowired
  private EmailProviderService emailProviderService;
  @Autowired
  private TemplateService      templateService;

  @Autowired
  private StoryCacheService storyCacheService;

  public void sendSubscriptionEmail(Subscription subscription) {
    var content = templateService.generateSubscriptionEmail(subscription);
    var email = Email.builder()
      .to(subscription.getEmail())
      .subject("[hntoplinks] - Welcome to hntoplinks.com")
      .html(content)
      .build();
    emailProviderService.send(email);
  }

  public void sendTopLinksEmail(TopLinksEmail topLinksEmail){
    var email = topLinksEmail.createEmail();
    emailProviderService.send(email);
  }

}
