package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class Page<T> {
  private String              title;
  private Map<String, Object> metadata;
  private T                   content;    // Specific page content

  // Optional components
  private Navigation navigation;
}
