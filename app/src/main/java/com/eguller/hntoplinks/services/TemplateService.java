package com.eguller.hntoplinks.services;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;

import lombok.Builder;
import lombok.Data;

@Service
public class TemplateService {
  @Autowired
  @Qualifier("emailTemplateEngine") private TemplateEngine templateEngine;

  @Value("${hntoplinks.base-url}")
  private String hntoplinksBaseUrl;

  public String generateSubscriptionEmail(SubscriberEntity subscriber) {
    var subscriptionEmailData =
        SubscriptionEmailData.builder()
            .baseUrl(hntoplinksBaseUrl)
            .unsubscribeUrl(hntoplinksBaseUrl + "/unsubscribe/" + subscriber.getSubsUUID())
            .build();
    final Context ctx = new Context(Locale.ENGLISH);
    ctx.setVariable("data", subscriptionEmailData);

    final String htmlContent =
        this.templateEngine.process("/email/html/subscription_email.html", ctx);
    return htmlContent;
  }

  public String generateTopEmail(
      String subject, SubscriberEntity subscriber, List<StoryEntity> topEmails) {
    var toplinksEmailData =
        TopEmailData.builder()
            .subject(subject)
            .unsubscribeUrl(hntoplinksBaseUrl + "/unsubscribe/" + subscriber.getSubsUUID())
            .updateSubscriptionUrl(
                hntoplinksBaseUrl + "/update-subscription/" + subscriber.getSubsUUID())
            .storyList(topEmails)
            .build();

    final Context ctx = new Context(Locale.ENGLISH);
    ctx.setVariable("data", toplinksEmailData);
    final String htmlContent = this.templateEngine.process("/email/html/toplinks_email.html", ctx);
    return htmlContent;
  }

  @Builder
  @Data
  private static class TopEmailData {
    private String subject;
    private List<StoryEntity> storyList;
    private String baseUrl;
    private String unsubscribeUrl;
    private String updateSubscriptionUrl;
  }

  @Builder
  @Data
  private static class SubscriptionEmailData {
    private String baseUrl;
    private String unsubscribeUrl;
  }
}
