package com.eguller.hntoplinks.services.email;

import com.eguller.hntoplinks.models.Email;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MockEmailStore {
  private final Map<String, List<Email>> emails = new HashMap<>();

  public void addEmail(Email email) {
    var emailList = emails.getOrDefault(email.getTo(), new ArrayList<>());
    emailList.add(email);
    emails.put(email.getTo(), emailList);
  }

  public Optional<Email> getLastMail(String emailAddress) {
    var emailList = emails.getOrDefault(emailAddress, new ArrayList<>());
    if(emailList.isEmpty()){
      return Optional.empty();
    }
    return Optional.ofNullable(emailList.get(emailList.size() - 1));
  }

  public int emailCount(String emailAddress){
    var emailList = emails.getOrDefault(emailAddress, new ArrayList<>());
    var size = emailList.size();
    return size;
  }

  public void reset() {
    emails.clear();
  }

  public void reset(String email){
    emails.remove(email);
  }
}
