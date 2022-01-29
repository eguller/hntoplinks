package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Singular;

import java.util.Set;

@Builder
public class Email {
  private final String      subject;
  private final String      plainText;
  private final String      html;
  @Singular("to")
  private final Set<String> to;
  @Singular("cc")
  private final Set<String> cc;
  @Singular("bcc")
  private final Set<String> bcc;
}
