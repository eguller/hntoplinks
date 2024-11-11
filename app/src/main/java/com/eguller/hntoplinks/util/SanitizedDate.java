package com.eguller.hntoplinks.util;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
@Builder
public class SanitizedDate {
  @NonNull
  @Getter
  private final Integer year;
  @Getter
  private final Integer month;
  @Getter
  private final Integer day;

  @Getter(lazy = true)
  private final String paddedMonth = month == null ? null :  String.format("%02d", month);
  @Getter(lazy = true)
  private final String paddedDay   = day == null ? null : String.format("%02d", day);
}
