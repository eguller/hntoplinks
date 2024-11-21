package com.eguller.hntoplinks.services;

import java.util.List;
import java.util.Locale;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;

import lombok.Builder;
import lombok.Data;

@Service
public class TemplateService {
  @Autowired
  @Qualifier("emailTemplateEngine")
  private TemplateEngine templateEngine;

  @Value("${hntoplinks.base-url}")
  private String hntoplinksBaseUrl;

  private Context ctx = new Context(Locale.ENGLISH);


  @PostConstruct
  public void init() {
    //ctx.setVariable("fmt", );
  }

  public String generateSubscriptionEmail(SubscriberEntity subscriber) {
    var periods =
      subscriber.getSubscriptionList().stream().map(SubscriptionEntity::getPeriod).toList();
    var sortedPeriods = periods.stream()
      .sorted(Period::compareTo)
      .toList();

    var subscriptionEmailData =
      SubscriptionEmailData.builder()
        .baseUrl(hntoplinksBaseUrl)
        .unsubscribeUrl(
          "%s/subscribers/%s?action=unsubscribe"
            .formatted(hntoplinksBaseUrl, subscriber.getSubscriberId()))
        .periods(sortedPeriods)
        .build();
    final Context ctx = new Context(Locale.ENGLISH);
    ctx.setVariable("data", subscriptionEmailData);

    final String htmlContent =
      this.templateEngine.process("/email/html/subscription_email.html", ctx);
    return htmlContent;
  }

  public String generateTopEmail(
    String subject, SubscriberEntity subscriber, List<Item> topEmails) {
    var toplinksEmailData =
      TopEmailData.builder()
        .subject(subject)
        .unsubscribeUrl(
          "%s/subscribers/%s?action=unsubscribe"
            .formatted(hntoplinksBaseUrl, subscriber.getSubscriberId()))
        .updateSubscriptionUrl(
          "%s/subscribers/%s".formatted(hntoplinksBaseUrl, subscriber.getSubscriberId()))
        .items(topEmails)
        .build();

    final Context ctx = new Context(Locale.ENGLISH);
    ctx.setVariable("data", toplinksEmailData);
    final String htmlContent = this.templateEngine.process("/email/html/toplinks_email.html", ctx);
    return htmlContent;
  }

  @Builder
  @Data
  private static class TopEmailData {
    private String     subject;
    private String     baseUrl;
    private String     unsubscribeUrl;
    private String     updateSubscriptionUrl;
    private List<Item> items;
  }

  @Builder
  @Data
  private static class SubscriptionEmailData {
    private String baseUrl;
    private String       unsubscribeUrl;
    private List<Period> periods;
  }
}
