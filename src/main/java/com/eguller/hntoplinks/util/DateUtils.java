package com.eguller.hntoplinks.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {
  public static String since(LocalDateTime time) {
    long yearsBetween = ChronoUnit.YEARS.between(time, LocalDateTime.now());
    if (yearsBetween == 1) {
      return yearsBetween + " year ago";
    } else if (yearsBetween > 1) {
      return yearsBetween + " years ago";
    }

    long monthsBetween = ChronoUnit.MONTHS.between(time, LocalDateTime.now());
    if (monthsBetween == 1) {
      return monthsBetween + " month ago";
    } else if (monthsBetween > 1) {
      return monthsBetween + " months ago";
    }

    long weeksBetween = ChronoUnit.WEEKS.between(time, LocalDateTime.now());
    if (weeksBetween == 1) {
      return weeksBetween + " week ago";
    } else if (weeksBetween > 1) {
      return weeksBetween + " weeks ago";
    }

    long daysBetween = ChronoUnit.DAYS.between(time, LocalDateTime.now());
    long hoursBetween = ChronoUnit.HOURS.between(time, LocalDateTime.now());
    if (hoursBetween >= 24) {
      if (daysBetween == 1) {
        return daysBetween + " day ago";
      } else if (daysBetween > 1) {
        return daysBetween + " days ago";
      }
    }

    if (hoursBetween == 1) {
      return hoursBetween + " hour ago";
    } else if (hoursBetween > 1) {
      return hoursBetween + " hours ago";
    }

    long minutesBetween = ChronoUnit.MINUTES.between(time, LocalDateTime.now());
    if (minutesBetween == 1) {
      return minutesBetween + " minute ago";
    } else if (minutesBetween > 1) {
      return minutesBetween + " minutes ago";
    } else {
      return "just now";
    }
  }

  public static ZoneId parseZoneId(String zoneId) {
    try {
      return ZoneId.of(zoneId);
    } catch (Exception ex) {
      return ZoneId.of("Etc/UTC");
    }
  }

  public static LocalDateTime tomorrow_7_AM(ZoneId targetZone) {
    return LocalDateTime.now(targetZone).plusDays(1).withHour(7).withMinute(0).withSecond(0);
  }

  public static LocalDateTime nextMonday_7_AM(ZoneId targetZone) {
    return LocalDateTime.now(targetZone).with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(7).withMinute(0).withSecond(0);
  }

  public static LocalDateTime firstDayOfNextMonth_7_AM(ZoneId targetZone) {
    return LocalDateTime.now(targetZone).with(TemporalAdjusters.firstDayOfNextMonth()).withHour(7).withMinute(0).withSecond(0);
  }

  public static LocalDateTime firstDayOfNextYear_7_AM(ZoneId targetZone) {
    return LocalDateTime.now(targetZone).with(TemporalAdjusters.firstDayOfNextYear()).withHour(7).withMinute(0).withSecond(0);
  }
}
