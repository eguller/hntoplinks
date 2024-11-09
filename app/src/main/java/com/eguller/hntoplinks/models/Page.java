package com.eguller.hntoplinks.models;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Page<T> {
  private String title;
  @Builder.Default
  private String currentPath = "/";
  private Map<String, Object> metadata;
  @Builder.Default
  private int selectedYear = LocalDateTime.now().getYear();
  private T content; // Specific page content

  // Optional components
  private Navigation navigation;
}
