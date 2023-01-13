package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Email;

import com.sendgrid.Content;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Service
@ConditionalOnProperty(value = "hntoplinks.email-provider", havingValue = "sendgrid", matchIfMissing = false)
public class SendGridEmailProviderService implements EmailProviderService {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${hntoplinks.sendgrid.api-key}")
  private String apiKey;

  @Value("${hntoplinks.mail.from}")
  private String from;

  @SneakyThrows
  @Override
  public void send(Email mail) {
    var fromAddr = new com.sendgrid.Email(from);
    var sendGrid = new SendGrid(apiKey);

    var toAddr = new com.sendgrid.Email(mail.getTo());
    var content = new Content("text/html", mail.getHtml());
    var sendgridMail = new Mail(fromAddr, mail.getSubject(), toAddr, content);
    var request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");

    request.setBody(sendgridMail.build());
    sendGrid.api(request);
  }
}
