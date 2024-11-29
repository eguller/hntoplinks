package com.eguller.hntoplinks.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.entities.Period;

public class FormattingUtils {

  private static final FormattingUtils INSTANCE = new FormattingUtils();

  private static final Map<Period, String> PERIOD_DESCRIPTION =
      Map.of(
          Period.DAILY, "Daily Updates",
          Period.WEEKLY, "Weekly Digest",
          Period.MONTHLY, "Monthly Roundup",
          Period.YEARLY, "Yearly Review");

  public static FormattingUtils getInstance() {
    return INSTANCE;
  }

  public String domainName(String url) {
    try {
      if (url == null) {
        return null;
      }
      URI uri = new URI(url);
      String domain = uri.getHost();
      if (domain != null) {
        return domain.startsWith("www.") ? domain.substring(4) : domain;
      } else {
        return extractDomainName(url);
      }
    } catch (URISyntaxException e) {
      return extractDomainName(url);
    }
  }

  private String extractDomainName(String url) {
    String tmpUrl = url;
    if (tmpUrl.startsWith("http://")) {
      tmpUrl = tmpUrl.substring("http://".length());
    }
    if (tmpUrl.startsWith("https://")) {
      tmpUrl = tmpUrl.substring("https://".length());
    }
    if (tmpUrl.startsWith("www.")) {
      tmpUrl = tmpUrl.substring("www.".length());
    }
    int urlPathSeperator = tmpUrl.indexOf("/");
    if (urlPathSeperator > -1) {
      return tmpUrl.substring(0, urlPathSeperator);
    } else {
      return tmpUrl;
    }
  }

  public String since(long timeL) {
    LocalDateTime time =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timeL), ZoneId.systemDefault());
    long yearsBetween = ChronoUnit.YEARS.between(time, LocalDateTime.now());
    if (yearsBetween >= 1) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
      return "on %s".formatted(time.format(formatter));
    }

    long monthsBetween = ChronoUnit.MONTHS.between(time, LocalDateTime.now());
    if (monthsBetween == 1) {
      return monthsBetween + " month ago";
    } else if (monthsBetween > 1) {
      return monthsBetween + " months ago";
    }

    long weeksBetween = ChronoUnit.WEEKS.between(time, LocalDateTime.now());
    if (weeksBetween == 1) {
      return weeksBetween + " week ago";
    } else if (weeksBetween > 1) {
      return weeksBetween + " weeks ago";
    }

    long daysBetween = ChronoUnit.DAYS.between(time, LocalDateTime.now());
    long hoursBetween = ChronoUnit.HOURS.between(time, LocalDateTime.now());
    if (hoursBetween >= 24) {
      if (daysBetween == 1) {
        return daysBetween + " day ago";
      } else if (daysBetween > 1) {
        return daysBetween + " days ago";
      }
    }

    if (hoursBetween == 1) {
      return hoursBetween + " hour ago";
    } else if (hoursBetween > 1) {
      return hoursBetween + " hours ago";
    }

    long minutesBetween = ChronoUnit.MINUTES.between(time, LocalDateTime.now());
    if (minutesBetween == 1) {
      return minutesBetween + " minute ago";
    } else if (minutesBetween > 1) {
      return minutesBetween + " minutes ago";
    } else {
      return "just now";
    }
  }

  public String url(Item item) {
    if (StringUtils.hasText(item.getUrl())) {
      return item.getUrl();
    } else {
      return "https://news.ycombinator.com/item?id=" + item.getId();
    }
  }

  public String periodDescription(Period period) {
    return PERIOD_DESCRIPTION.getOrDefault(period, "");
  }
}
