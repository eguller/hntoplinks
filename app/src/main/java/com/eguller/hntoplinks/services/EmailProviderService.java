package com.eguller.hntoplinks.services;


import com.eguller.hntoplinks.models.Email;
import jakarta.mail.MessagingException;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.io.UnsupportedEncodingException;
import java.util.Set;

public interface EmailProviderService {
  void send(Email mail);


}
