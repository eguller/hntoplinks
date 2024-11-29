package com.eguller.hntoplinks.entities;

import java.util.Map;

public enum SortType {
  UPVOTES("upvotes"),
  COMMENTS("comments");

  private final String value;

  private static final Map<String, SortType> sortTypeMap =
      Map.of(
          "upvotes", UPVOTES,
          "comments", COMMENTS);

  SortType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static SortType fromString(String value) {
    if (value == null) return null;

    return sortTypeMap.getOrDefault(value.toLowerCase(), UPVOTES);
  }
}
