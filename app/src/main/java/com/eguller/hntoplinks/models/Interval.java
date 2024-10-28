package com.eguller.hntoplinks.models;

import java.time.LocalDateTime;

public record Interval(LocalDateTime from, LocalDateTime to) {
  public Interval {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
