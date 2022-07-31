package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuperBuilder
public class DailyTopLinksEmail extends TopLinksEmail {
  @Override
  protected String createSubject() {
    String timePrefix = DateTimeFormatter.ofPattern("EEEE, dd MMMM").format(LocalDateTime.now().minusDays(1).atZone(subscription.getTimeZone()));
    return timePrefix + " - Daily Top Links";
  }

  @Override
  protected List<Story> getTopStories() {
    return storyCacheService.getDailyTop();
  }
}
