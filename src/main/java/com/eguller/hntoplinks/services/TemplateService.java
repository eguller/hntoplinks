package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Story;
import com.eguller.hntoplinks.models.Subscription;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class TemplateService {
  @Autowired
  @Qualifier("emailTemplateEngine")
  private TemplateEngine templateEngine;

  @Value("${hntoplinks.base-url}")
  private String hntoplinksBaseUrl;

  public String generateSubscriptionEmail(Subscription subscription) {
    var subscriptionEmailData = SubscriptionEmailData.builder()
      .unsubscribeUrl(hntoplinksBaseUrl + "/unsubscribe/" + subscription.getSubsUUID()).build();
    final Context ctx = new Context(Locale.ENGLISH);
    ctx.setVariable("data", subscriptionEmailData);

    final String htmlContent = this.templateEngine.process("/html/subscription_email.html", ctx);
    return htmlContent;
  }

  public String generateTopEmail(String subject, Subscription subscription, List<Story> topEmails) {
    var toplinksEmailData = TopEmailData.builder()
      .subject(subject)
      .unsubscribUrl(hntoplinksBaseUrl + "/unsubscribe/" + subscription.getSubsUUID())
      .updateSubscriptionUrl(hntoplinksBaseUrl + "/update-subscription/" + subscription.getSubsUUID())
      .storyList(topEmails).build();

    final Context ctx = new Context(Locale.ENGLISH);
    ctx.setVariable("data", toplinksEmailData);
    final String htmlContent = this.templateEngine.process("/html/toplinks_email.html", ctx);
    return htmlContent;
  }


  @Builder
  @Data
  private static class TopEmailData {
    private String      subject;
    private List<Story> storyList;
    private String      unsubscribUrl;
    private String      updateSubscriptionUrl;

  }

  @Builder
  @Data
  private static class SubscriptionEmailData {
    private String unsubscribeUrl;
  }
}
