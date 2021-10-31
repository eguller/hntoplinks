package com.eguller.hntoplinks.jobs;

import com.eguller.hntoplinks.models.Statistics;
import com.eguller.hntoplinks.services.StatisticsCacheService;
import com.eguller.hntoplinks.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

@Component
public class ReloadStatisticsJob {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private StatisticsCacheService statisticsCacheService;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void reload(){
        logger.info("Starting loading statistics.");
        Statistics statistics = statisticsService.readStatistics();
        statisticsCacheService.setStatistics(statistics);
        logger.info("Loading statistics completed.");
    }
}
