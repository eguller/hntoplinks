package com.eguller.hntoplinks.util;

import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eguller.hntoplinks.models.Interval;

public class DateUtils {
  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public static final int HN_LAUNCH_YEAR = 2006;

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
    return ZonedDateTime.now(targetZone)
        .plusDays(1)
        .withHour(7)
        .withMinute(0)
        .withSecond(0)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime();
  }

  public static LocalDateTime nextMonday_7_AM(ZoneId targetZone) {
    return ZonedDateTime.now(targetZone)
        .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        .withHour(7)
        .withMinute(0)
        .withSecond(0)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime();
  }

  public static LocalDateTime firstDayOfNextMonth_7_AM(ZoneId targetZone) {
    return ZonedDateTime.now(targetZone)
        .with(TemporalAdjusters.firstDayOfNextMonth())
        .withHour(7)
        .withMinute(0)
        .withSecond(0)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime();
  }

  public static LocalDateTime firstDayOfNextYear_7_AM(ZoneId targetZone) {
    return ZonedDateTime.now(targetZone)
        .with(TemporalAdjusters.firstDayOfNextYear())
        .withHour(7)
        .withMinute(0)
        .withSecond(0)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime();
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
    var now = LocalDateTime.now();
    return Interval.of(now.minusDays(1), now);
  }

  public static Interval getIntervalForYesterday(ZoneId zoneId) {
    LocalDate yesterday = LocalDate.now(zoneId).minusDays(1);

    LocalDateTime start = yesterday.atStartOfDay();
    LocalDateTime end = yesterday.atTime(LocalTime.MAX);

    return Interval.of(start, end); // Assuming static factory method
  }

  public static Interval getIntervalForLastWeek() {
    LocalDateTime now = LocalDateTime.now();
    return Interval.of(now.minusDays(7), now);
  }

  public static Interval getIntervalForLastWeek(ZoneId zoneId) {
    LocalDate now = LocalDate.now(zoneId);

    LocalDate lastSunday = now.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
    LocalDate lastMonday = lastSunday.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));

    LocalDateTime start = lastMonday.atStartOfDay();
    LocalDateTime end = lastSunday.atTime(LocalTime.MAX);

    return Interval.of(start, end);
  }

  public static Interval getIntervalForLastMonth() {
    LocalDateTime now = LocalDateTime.now();
    return Interval.of(now.minusMonths(1), now);
  }

  public static Interval getIntervalForLastMonth(ZoneId zoneId) {
    LocalDate now = LocalDate.now(zoneId);

    // Get first day of last month
    LocalDate firstDayOfLastMonth = now.minusMonths(1).withDayOfMonth(1);

    // Get last day of last month
    LocalDate lastDayOfLastMonth = now.withDayOfMonth(1).minusDays(1);

    LocalDateTime start = firstDayOfLastMonth.atStartOfDay();
    LocalDateTime end = lastDayOfLastMonth.atTime(LocalTime.MAX);

    return Interval.of(start, end);
  }

  public static Interval getIntervalForLastYear() {
    var now = LocalDateTime.now();
    return Interval.of(now.minusYears(1), now);
  }

  public static Interval getIntervalForLastYear(ZoneId zoneId) {
    LocalDate now = LocalDate.now(zoneId);

    // Get first day of last year
    LocalDate firstDayOfLastYear = now.minusYears(1).withDayOfYear(1);

    // Get last day of last year
    LocalDate lastDayOfLastYear = now.withDayOfYear(1).minusDays(1);

    LocalDateTime start = firstDayOfLastYear.atStartOfDay();
    LocalDateTime end = lastDayOfLastYear.atTime(LocalTime.MAX);

    return Interval.of(start, end);
  }

  public static Interval getInterval(int year) {
    var from = LocalDate.of(year, 1, 1).atStartOfDay();
    var to = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
    return Interval.of(from, to);
  }

  public static Interval getInterval(int year, int month) {
    var validatedMonth = month < 1 ? 1 : month > 12 ? 12 : month;
    var from = LocalDate.of(year, validatedMonth, 1).atStartOfDay();
    var maxDay = YearMonth.of(year, validatedMonth).lengthOfMonth();
    var to = LocalDate.of(year, validatedMonth, maxDay).atTime(LocalTime.MAX);
    return Interval.of(from, to);
  }

  public static Interval getInterval(int year, int month, int day) {
    var validatedMonth = month < 1 ? 1 : month > 12 ? 12 : month;
    // get max days in a given month
    var maxDay = YearMonth.of(year, validatedMonth).lengthOfMonth();
    var validatedDay = day < 1 ? 1 : day > maxDay ? maxDay : day;

    var date = LocalDate.of(year, validatedMonth, validatedDay);
    var from = date.atStartOfDay();
    var to = date.atTime(LocalTime.MAX);
    return Interval.of(from, to);
  }

  public static List<Integer> getYears() {
    return Stream.iterate(LocalDateTime.now().getYear(), i -> i >= HN_LAUNCH_YEAR, i -> i - 1)
        .toList();
  }

  public static List<Month> getMonthsForYear(Integer year) {
    int start;
    int end;
    if (year == null || year == LocalDate.now().getYear()) {
      start = 1;
      end = LocalDate.now().getMonthValue();
    } else if (year == 2006) {
      start = 10; // hacker news first story posted in '6 Oct 2006'
      end = 12;
    } else {
      start = 1;
      end = 12;
    }
    return IntStream.iterate(end, i -> i >= start, i -> i - 1).mapToObj(Month::of).toList();
  }

  public static String getDisplayName(Month month) {
    return month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
  }

  public static String getDisplayName(int month) {
    String monthName = getDisplayName(Month.of(month));
    return monthName;
  }

  public static SanitizedDate sanitizeYear(Integer year) {
    return sanitizeDate(year, null, null);
  }

  public static SanitizedDate sanitizedDate(Integer year, Integer month) {
    return sanitizeDate(year, month, null);
  }

  public static SanitizedDate sanitizeDate(Integer year, Integer month, Integer day) {
    LocalDate now = LocalDate.now();
    int currentYear = now.getYear();

    // Sanitize year
    int sanitizedYear =
        Optional.ofNullable(year)
            .map(y -> Math.min(Math.max(y, 2006), currentYear))
            .orElse(currentYear);

    // Sanitize month
    Integer sanitizedMonth;
    if (month != null) {
      if (sanitizedYear == currentYear) {
        sanitizedMonth =
            Optional.ofNullable(month)
                .map(m -> Math.min(Math.max(m, 1), now.getMonthValue()))
                .orElse(null);
      } else {
        sanitizedMonth =
            Optional.ofNullable(month).map(m -> Math.min(Math.max(m, 1), 12)).orElse(null);
      }
    } else {
      sanitizedMonth = null;
    }

    // Sanitize day
    Integer sanitizedDay;
    if (sanitizedMonth != null && day != null) {
      int maxDay = YearMonth.of(sanitizedYear, sanitizedMonth).lengthOfMonth();
      if (sanitizedYear == currentYear && sanitizedMonth == now.getMonthValue()) {
        maxDay = now.getDayOfMonth();
      }

      int maxDayF = maxDay;

      sanitizedDay =
          Optional.ofNullable(day).map(d -> Math.min(Math.max(d, 1), maxDayF)).orElse(maxDayF);
    } else {
      sanitizedDay = null;
    }

    return SanitizedDate.builder()
        .year(sanitizedYear)
        .month(sanitizedMonth)
        .day(sanitizedDay)
        .build();
  }
}
