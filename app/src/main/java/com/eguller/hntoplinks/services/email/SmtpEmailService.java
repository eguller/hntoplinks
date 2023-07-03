package com.eguller.hntoplinks.services.email;

import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.services.EmailProviderService;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.Properties;


public abstract class SmtpEmailService implements EmailProviderService {
  private static final Logger     logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final        SmtpConfig smtpConfig;
  private final        Session    session;

  public SmtpEmailService(SmtpConfig smtpConfig) {
    this.smtpConfig = smtpConfig;
    final var props = new Properties();

    props.put("mail.smtp.host", smtpConfig.host()); //SMTP Host
    props.put("mail.smtp.port", smtpConfig.port()); //TLS Port
    props.put("mail.smtp.auth", "true"); //enable authentication
    props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

    //create Authenticator object to pass in Session.getInstance argument
    Authenticator auth = new Authenticator() {
      //override the getPasswordAuthentication method
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(smtpConfig.username(), smtpConfig.password());
      }
    };
    session = Session.getInstance(props, auth);
  }

  @Override
  public void send(Email mail) {
    MimeMessage msg = new MimeMessage(session);
    try {
      //set message headers
      msg.addHeader("Content-type", "text/html; charset=UTF-8");
      msg.addHeader("format", "flowed");

      msg.setFrom(new InternetAddress(smtpConfig.from(), "Hacker News Top Links"));
      msg.setReplyTo(InternetAddress.parse(smtpConfig.from(), false));
      msg.setSubject(mail.getSubject(), "UTF-8");
      msg.setContent(mail.getHtml(), "text/html; charset=UTF-8");
      msg.setSentDate(new Date());

      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getTo(), false));
      Transport.send(msg);
    } catch (Exception ex) {
      throw new RuntimeException("Email could not be sent.", ex);
    }
  }
}
