package com.eguller.hntoplinks.services.email;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

@Service
@ConditionalOnProperty(value = "hntoplinks.mail.provider", havingValue = "sendpulse")
public class SendPulseEmailService extends SmtpEmailService {
  public SendPulseEmailService(SendPulseSmtpConfig config) {
    super(config);
  }
}
