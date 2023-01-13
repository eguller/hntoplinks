package com.eguller.hntoplinks.services;


import com.eguller.hntoplinks.models.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

public interface EmailProviderService {
  void send(Email mail);


}
