package com.eguller.hntoplinks.services.email;

public interface SmtpConfig {
  String from();

  String fromName();

  String host();

  String port();

  String username();

  String password();
}
