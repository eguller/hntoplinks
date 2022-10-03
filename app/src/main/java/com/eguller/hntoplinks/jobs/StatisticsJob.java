package com.eguller.hntoplinks.jobs;

import com.eguller.hntoplinks.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class StatisticsJob {
  @Autowired
  private StatisticsService statisticsService;
  @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
  public void updateStatistics() {
    statisticsService.updateStatistics();
  }
}
