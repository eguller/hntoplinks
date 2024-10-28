package com.eguller.hntoplinks.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class FormattingUtils {
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
    if (yearsBetween == 1) {
      return yearsBetween + " year ago";
    } else if (yearsBetween > 1) {
      return yearsBetween + " years ago";
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
}
