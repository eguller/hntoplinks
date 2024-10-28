package com.eguller.hntoplinks.util;

public class DbUtils {
  public static int pageToOffset(int page, int pageSize) {
    var validatedPage = page < 1 ? 1 : page;
    return (validatedPage - 1) * pageSize;
  }
}
