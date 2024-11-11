package com.eguller.hntoplinks.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Navigation {
  @Builder.Default private List<Breadcrumb> breadcrumbs = new ArrayList<>();

  @Builder.Default private String activeMenu = "day";
}
