package com.eguller.hntoplinks.models;

import java.util.Arrays;
import java.util.Optional;

public enum PageTab {
  today,
  week,
  month,
  year,
  all,
  subscribe;

  public static Optional<PageTab> fromString(String pageTabStr) {
    return Arrays.stream(PageTab.values())
        .filter(pageTab -> pageTab.name().equalsIgnoreCase(pageTabStr))
        .findAny();
  }

  public String getTitleText() {
    return switch (this) {
      case today -> "Today - Hacker News Top Links";
      case week -> "Week - Hacker News Top Links";
      case month -> "Month - Hacker News Top Links";
      case year -> "Year - Hacker News Top Links";
      case all -> "All Time Best - Hacker News Top Links";
      case subscribe -> "Subscribe";
      default -> "Hacker News Top Links";
    };
  }
}
