package com.eguller.hntoplinks.services.email;

import com.eguller.hntoplinks.services.EmailProviderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "hntoplinks.mail.provider", havingValue = "maildev")
public class MailDevSmtpService extends SmtpEmailService {
  public MailDevSmtpService(MailDevSmtpConfig config) {
    super(config);
  }
}
