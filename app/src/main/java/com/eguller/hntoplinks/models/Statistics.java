package com.eguller.hntoplinks.models;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Statistics {
  private long subscriberCount;
  private long dailySubscriberCount;
  private long weeklySubscriberCount;
  private long monthlySubscriberCount;
  private long annuallySubscriberCount;
  private long unsubscribeCount;
  private long successEmailCount;
  private long failureEmailCount;
  private LocalDateTime lastEmailSent;
  private LocalDateTime lastHnUpdate;
}
