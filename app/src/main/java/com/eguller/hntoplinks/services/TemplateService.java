package com.eguller.hntoplinks.services;

import java.util.List;
import java.util.Locale;

import com.eguller.hntoplinks.util.FormattingUtils;
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

    var ctx = new Context(Locale.ENGLISH);
    ctx.setVariable("data", subscriptionEmailData);
    final String htmlContent =
      this.templateEngine.process("/email/html/subscription_email.html", ctx);
    return htmlContent;
  }

  public String generateTopEmail(
    String subject, SubscriptionEntity subscription, SubscriberEntity subscriber, List<Item> topEmails) {
    var toplinksEmailData =
      TopEmailData.builder()
        .subject(subject)
        .periodDescription(FormattingUtils.getInstance().periodDescription(subscription.getPeriod()))
        .period(subscription.getPeriod())
        .unsubscribeUrl(
          "%s/subscribers/%s?action=unsubscribe"
            .formatted(hntoplinksBaseUrl, subscriber.getSubscriberId()))
        .manageEmailFrequencyUrl(
          "%s/subscribers/%s".formatted(hntoplinksBaseUrl, subscriber.getSubscriberId()))
        .stories(topEmails)
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
    private String     manageEmailFrequencyUrl;
    private String periodDescription;
    private Period period;
    private List<Item> stories;
  }

  @Builder
  @Data
  private static class SubscriptionEmailData {
    private String baseUrl;
    private String       unsubscribeUrl;
    private List<Period> periods;
  }
}
