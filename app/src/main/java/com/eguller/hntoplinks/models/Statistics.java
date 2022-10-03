package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Statistics {
  private long          subscriberCount;
  private long          dailySubscriberCount;
  private long          weeklySubscriberCount;
  private long          monthlySubscriberCount;
  private long          annuallySubscriberCount;
  private long          unsubscribeCount;
  private long          successEmailCount;
  private long          failureEmailCount;
  private LocalDateTime lastEmailSent;
  private LocalDateTime lastHnUpdate;
}
