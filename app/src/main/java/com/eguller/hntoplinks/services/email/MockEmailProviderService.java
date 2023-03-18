package com.eguller.hntoplinks.services.email;

import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.services.EmailProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
@ConditionalOnProperty(value = "hntoplinks.mail.provider", havingValue = "mock")
public class MockEmailProviderService implements EmailProviderService {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  @Autowired
  MockEmailStore mockEmailStore;

  @Override
  public void send(Email mail) {
    mockEmailStore.addEmail(mail);
    logger.info("Sending email. to=%s, subject: %s, content=%d".formatted(mail.getTo(), mail.getSubject(), mail.getHtml().length()));
  }
}
