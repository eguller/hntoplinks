package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Breadcrumb {
  private final String title;

  private final String url; // URL can be null for current page

  public static Breadcrumb link(String title, String url) {
    return Breadcrumb.builder().title(title).url(url).build();
  }
}
