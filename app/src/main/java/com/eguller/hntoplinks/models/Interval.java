package com.eguller.hntoplinks.models;

import java.time.LocalDateTime;

public record Interval(LocalDateTime from, LocalDateTime to) {
  private Interval(LocalDateTime from, LocalDateTime to) {
    if (from == null || to == null) {
      throw new IllegalArgumentException("Dates cannot be null");
    }
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("Start date must be before end date");
    }
    this.from = from;
    this.to  = to;
  }

  public static Interval of(LocalDateTime start, LocalDateTime end) {
    return new Interval(start, end);
  }
}
