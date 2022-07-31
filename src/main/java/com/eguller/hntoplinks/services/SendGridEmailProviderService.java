package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Email;
import com.sendgrid.SendGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "hntoplinks.email-provider", havingValue = "sendgrid", matchIfMissing = false)
public class SendGridEmailProviderService implements EmailProviderService {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${hntoplinks.sendgrid.api-key}")
  private String apiKey;

  @Override
  public void send(Email mail) {
    SendGrid sendgrid = new SendGrid(apiKey);

    SendGrid.Email email = new SendGrid.Email();
    for (var to : mail.getTo()) {
      email.addTo(to);
    }
    email.setFrom("toplinks@hntoplinks.com");
    email.setSubject(mail.getSubject());
    email.setHtml(mail.getHtml());
    //email.setText(textContent);
    email.setFromName("Hacker News Top Links");
    //addHeaders(email, headers);

    try {
      SendGrid.Response response = sendgrid.send(email);
      if (!response.getStatus()) {
        throw new RuntimeException("Sending email has failed. sendgridResponseCode=" + response.getCode() + ", " +
          "sendgridResponse=" + response.getMessage());
      }
    } catch (Exception e) {
      throw new RuntimeException("Sending email has failed.", e);
    }
  }

  @Override
  public void sendAsync(Email email) {
    send(email);
  }

  private void addHeaders(SendGrid.Email email, Map<String, String> headers) {
    for (Map.Entry<String, String> header : headers.entrySet()) {
      email.addHeader(header.getKey(), header.getValue());
    }
  }
}
