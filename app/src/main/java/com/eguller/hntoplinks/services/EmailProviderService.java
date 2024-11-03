package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Email;

public interface EmailProviderService {
  void send(Email mail);
}
