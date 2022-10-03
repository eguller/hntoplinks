package com.eguller.hntoplinks.models;

import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuperBuilder
public class MonthlyTopLinksEmail extends TopLinksEmail {
  @Override
  protected String createSubject() {
    String lmString = DateTimeFormatter.ofPattern("dd MMMM").format(LocalDateTime.now().minusMonths(1).atZone(subscription.getTimeZone()));
    return lmString + " - Best of Last Month";
  }

  @Override
  protected List<Story> getTopStories() {
    return storyCacheService.getMonthlyTop();
  }
}
