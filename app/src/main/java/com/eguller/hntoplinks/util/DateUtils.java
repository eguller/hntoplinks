package com.eguller.hntoplinks.util;

import com.eguller.hntoplinks.models.Interval;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateUtils {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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

  public static ZoneId parseZoneId(String zoneIdStr) {
    var zoneId = ZoneId.systemDefault();
    try {
      zoneId = ZoneId.of(zoneIdStr);
    } catch (Exception ex) {
      logger.error("zoneId could not be parsed. zoneId=" + zoneIdStr);
    }
    return zoneId;
  }

  public static LocalDateTime tomorrow_7_AM(ZoneId targetZone) {
    return ZonedDateTime.now(targetZone).plusDays(1).withHour(7).withMinute(0).withSecond(0).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static LocalDateTime nextMonday_7_AM(ZoneId targetZone) {
    return ZonedDateTime.now(targetZone).with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(7).withMinute(0).withSecond(0).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static LocalDateTime firstDayOfNextMonth_7_AM(ZoneId targetZone) {
    return ZonedDateTime.now(targetZone).with(TemporalAdjusters.firstDayOfNextMonth()).withHour(7).withMinute(0).withSecond(0).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static LocalDateTime firstDayOfNextYear_7_AM(ZoneId targetZone) {
    return ZonedDateTime.now(targetZone).with(TemporalAdjusters.firstDayOfNextYear()).withHour(7).withMinute(0).withSecond(0).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static ZoneId zoneOf(String zoneIdStr) {
    var zoneId = ZoneId.systemDefault();
    try {
      zoneId = ZoneId.of(zoneIdStr);
    } catch (Exception ex) {

    }
    return zoneId;
  }

  public static Interval getIntervalForToday() {
    val now = LocalDate.now();
    return getInterval(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
  }

  public static Interval getIntervalForCurrentWeek() {
    var now = LocalDate.now();
    var weekFields = WeekFields.of(Locale.getDefault());
    LocalDateTime from = now.with(weekFields.dayOfWeek(), 1).atStartOfDay();
    LocalDateTime to = now.with(weekFields.dayOfWeek(), 7).atTime(LocalTime.MAX);
    return new Interval(from, to);
  }

  public static Interval getIntervalForCurrentMonth() {
    val now = LocalDate.now();
    return getInterval(now.getYear(), now.getMonthValue());
  }

  public static Interval getIntervalForCurrentYear() {
    val now = LocalDate.now();
    return getInterval(now.getYear());
  }

  public static Interval getInterval(int year) {
    var from = LocalDate.of(year, 1, 1).atStartOfDay();
    var to = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
    return new Interval(from, to);
  }

  public static Interval getInterval(int year, int month) {
    var validatedMonth = month < 1 ? 1 : month > 12 ? 12 : month;
    var from = LocalDate.of(year, validatedMonth, 1).atStartOfDay();
    var maxDay = YearMonth.of(year, validatedMonth).lengthOfMonth();
    var to = LocalDate.of(year, validatedMonth, maxDay).atTime(LocalTime.MAX);
    return new Interval(from, to);
  }

  public static Interval getInterval(int year, int month, int day) {
    var validatedMonth = month < 1 ? 1 : month > 12 ? 12 : month;
    // get max days in a given month
    var maxDay = YearMonth.of(year, validatedMonth).lengthOfMonth();
    var validatedDay = day < 1 ? 1 : day > maxDay ? maxDay : day;

    var date = LocalDate.of(year, validatedMonth, validatedDay);
    var from = date.atStartOfDay();
    var to = date.atTime(LocalTime.MAX);
    return new Interval(from, to);
  }


}
