package com.eguller.hntoplinks.services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailDevSmtpConfig implements SmtpConfig {
  private final String from;
  private final String fromName;
  private final String host;
  private final String port;

  public MailDevSmtpConfig(
      @Value("${hntoplinks.mail.from.email}") String from,
      @Value("${hntoplinks.mail.from.name}") String fromName,
      @Value("${hntoplinks.mail.maildev.host}") String host,
      @Value("${hntoplinks.mail.maildev.port}") String port) {
    this.from = from;
    this.fromName = fromName;
    this.host = host;
    this.port = port;
  }

  @Override
  public String from() {
    return from;
  }

  @Override
  public String fromName() {
    return fromName;
  }

  @Override
  public String host() {
    return host;
  }

  @Override
  public String port() {
    return port;
  }

  @Override
  public String username() {
    return "";
  }

  @Override
  public String password() {
    return "";
  }
}
