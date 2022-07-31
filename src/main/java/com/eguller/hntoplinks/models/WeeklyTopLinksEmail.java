package com.eguller.hntoplinks.models;

import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuperBuilder
public class WeeklyTopLinksEmail extends TopLinksEmail {
  @Override
  protected String createSubject() {
    String toDate = DateTimeFormatter.ofPattern("dd MMMM").format(LocalDateTime.now().minusDays(1).atZone(subscription.getTimeZone()));
    String fromDate = DateTimeFormatter.ofPattern("dd MMMM").format(LocalDateTime.now().minusDays(7).atZone(subscription.getTimeZone()));
    return String.format("%s - %s Weekly Top Links", fromDate, toDate);
  }

  @Override
  protected List<Story> getTopStories() {
    return storyCacheService.getWeeklTop();
  }
}
