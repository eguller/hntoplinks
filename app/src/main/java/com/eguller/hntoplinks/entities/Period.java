package com.eguller.hntoplinks.entities;

import java.time.LocalDateTime;

import com.eguller.hntoplinks.util.DateUtils;

public enum Period {
  DAILY,
  WEEKLY,
  MONTHLY,
  YEARLY;

  public LocalDateTime nextSendDate(String timeZone) {
    var nextSendDate =
        switch (this) {
          case DAILY -> DateUtils.tomorrow_7_AM(DateUtils.zoneOf(timeZone));
          case WEEKLY -> DateUtils.nextMonday_7_AM(DateUtils.zoneOf(timeZone));
          case MONTHLY -> DateUtils.firstDayOfNextMonth_7_AM(DateUtils.zoneOf(timeZone));
          case YEARLY -> DateUtils.firstDayOfNextYear_7_AM(DateUtils.zoneOf(timeZone));
        };
    return nextSendDate;
  }
}
