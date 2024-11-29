package com.eguller.hntoplinks.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.eguller.hntoplinks.entities.SortType;

@Component
public class StringToEnumConverter implements Converter<String, SortType> {
  @Override
  public SortType convert(String source) {
    try {
      return SortType.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      return SortType.UPVOTES;
    }
  }
}
