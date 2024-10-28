package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Navigation {
    private List<Breadcrumb> breadcrumbs;
    private String activeMenu;
}
