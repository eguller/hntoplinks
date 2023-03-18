package com.eguller.hntoplinks.services.email;

import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.models.TopLinksEmail;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

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
