package com.eguller.hntoplinks.models;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Navigation {
  private List<Breadcrumb> breadcrumbs;
  private String activeMenu;
}
