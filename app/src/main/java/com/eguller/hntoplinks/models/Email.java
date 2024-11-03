package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Email {
  private final String subject;
  private final String plainText;
  private final String html;
  private final String to;
}
