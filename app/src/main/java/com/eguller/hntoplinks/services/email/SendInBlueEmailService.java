package com.eguller.hntoplinks.services.email;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "hntoplinks.mail.provider", havingValue = "sendinblue")
public class SendInBlueEmailService extends SmtpEmailService {
  public SendInBlueEmailService(SendPulseSmtpConfig config) {
    super(config);
  }
}
