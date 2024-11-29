package com.eguller.hntoplinks.services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendPulseSmtpConfig implements SmtpConfig {
  private final String from;
  private final String fromName;
  private final String host;
  private final String port;
  private final String username;
  private final String password;

  public SendPulseSmtpConfig(
      @Value("${hntoplinks.mail.from.email}") String from,
      @Value("${hntoplinks.mail.from.name}") String fromName,
      @Value("${hntoplinks.mail.sendpulse.smtp.host}") String host,
      @Value("${hntoplinks.mail.sendpulse.smtp.port}") String port,
      @Value("${hntoplinks.mail.sendpulse.smtp.username}") String username,
      @Value("${hntoplinks.mail.sendpulse.smtp.password}") String password) {
    this.from = from;
    this.fromName = fromName;
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
  }

  @Override
  public String from() {
    return this.from;
  }

  @Override
  public String fromName() {
    return this.fromName;
  }

  @Override
  public String host() {
    return this.host;
  }

  @Override
  public String port() {
    return this.port;
  }

  @Override
  public String username() {
    return this.username;
  }

  @Override
  public String password() {
    return this.password;
  }
}
