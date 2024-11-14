package com.eguller.hntoplinks.models;

import java.time.LocalDateTime;

public record Interval(LocalDateTime from, LocalDateTime to) {
  // Public compact constructor
  public Interval {
    if (from == null || to == null) {
      throw new IllegalArgumentException("Dates cannot be null");
    }
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("Start date must be before end date");
    }
  }

  // Static factory method
  public static Interval of(LocalDateTime start, LocalDateTime end) {
    return new Interval(start, end);
  }
}
