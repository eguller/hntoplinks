package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.models.TopLinksEmail;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MockEmailStore {
  private final Map<String, Email> emails = new HashMap<>();

  public void addEmail(Email email) {
    email.getTo().forEach(to -> emails.put(to, email));
  }

  public Optional<Email> getLastMail(String emailAddress) {
    return Optional.ofNullable(emails.get(emailAddress));
  }

  public void reset() {
    emails.clear();
  }

  public void reset(String email){
    emails.remove(email);
  }
}
