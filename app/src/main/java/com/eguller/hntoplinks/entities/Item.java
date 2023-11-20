package com.eguller.hntoplinks.entities;

import java.time.LocalDateTime;

public record Item(
  String id,
  Long hnid,
  String storyType,
  LocalDateTime createdAt,
  String url,
  Integer score,
  String title,
  Integer commentCount,
  Long version
) {
}
