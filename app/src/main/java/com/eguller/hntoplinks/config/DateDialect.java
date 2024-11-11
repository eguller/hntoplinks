package com.eguller.hntoplinks.config;

import com.eguller.hntoplinks.util.DateUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Component
public class DateDialect extends AbstractDialect implements IExpressionObjectDialect {

  public DateDialect() {
    super("date");
  }

  @Override
  public IExpressionObjectFactory getExpressionObjectFactory() {
    return new IExpressionObjectFactory() {

      @Override
      public Set<String> getAllExpressionObjectNames() {
        return Set.of("dateUtils");
      }

      @Override
      public Object buildObject(IExpressionContext context, String expressionObjectName) {
        return new DateUtilMethods();  // Wrapper class with instance methods
      }

      @Override
      public boolean isCacheable(String expressionObjectName) {
        return true;
      }
    };
  }

  // Wrapper class
  public static class DateUtilMethods {
    public List<Month> getMonthsForYear(Integer year) {
      return DateUtils.getMonthsForYear(year);  // Delegates to static method
    }

    public List<Integer> getYears() {
      return DateUtils.getYears();  // Delegates to static method
    }

    public String getDisplayName(Month month) {
      return DateUtils.getDisplayName(month);  // Delegates to static method
    }

    public LocalDateTime now() {
      return LocalDateTime.now();
    }

    public int year(LocalDateTime time) {
      return time.getYear();
    }

    public int currentYear() {
      return LocalDateTime.now().getYear();
    }

    public int getYearOrCurrent(Integer year) {
      return year == null ? LocalDateTime.now().getYear() : year;
    }

    public String getPaddedMonth(Integer month) {
      return String.format("%02d", month);
    }
  }

}

