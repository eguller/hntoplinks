package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Email;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

@Service
@ConditionalOnProperty(value = "hntoplinks.email-provider", havingValue = "mandrill", matchIfMissing = false)
public class MandrillEmailProviderService implements EmailProviderService {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  @Value("${hntoplinks.mandrill.api-key}")
  private String              apiKey;
  @Override
  public void send(Email mail) {
    MandrillApi mandrillApi = new MandrillApi(apiKey);
    MandrillMessage message = new MandrillMessage();
    message.setSubject(mail.getSubject());
    message.setHtml(mail.getHtml());
    message.setAutoText(true);
    message.setFromEmail("toplinks@hntoplinks.com");
    message.setFromName("Hacker News Top Links");
    //message.setHeaders(headers);
    //message.setText(textContent);
    ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();

    for(String recipientEmail : mail.getTo()){
      MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
      recipient.setEmail(recipientEmail);
      recipients.add(recipient);
    }

    message.setTo(recipients);
    message.setPreserveRecipients(true);
    try {
      MandrillMessageStatus[] messageStatusReports = mandrillApi
        .messages().send(message, false);
      for (MandrillMessageStatus status : messageStatusReports) {
        logger.info("Message sent. id: {}, email: {}, status: {}, rejection reason: {}", status.getId(), status.getEmail(), status.getStatus(), status.getRejectReason());
      }
    } catch (Exception ex) {
      throw new RuntimeException("Sending email has failed.", ex);
    }
  }
}
