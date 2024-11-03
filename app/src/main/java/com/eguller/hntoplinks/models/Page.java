package com.eguller.hntoplinks.models;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Page<T> {
  private String title;
  private Map<String, Object> metadata;
  private T content; // Specific page content

  // Optional components
  private Navigation navigation;
}
