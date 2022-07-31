package com.eguller.hntoplinks.models;

import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuperBuilder
public class AnnuallyTopLinksEmail extends TopLinksEmail {

  @Override
  protected String createSubject() {
    String lastYear = DateTimeFormatter.ofPattern("YYYY").format(LocalDateTime.now().minusYears(1).atZone(subscription.getTimeZone()));
    return "Best of " + lastYear;
  }

  @Override
  protected List<Story> getTopStories() {
    return storyCacheService.getAnnuallyTop();
  }
}
