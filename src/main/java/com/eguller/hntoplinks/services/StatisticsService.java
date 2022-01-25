package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.StatKey;
import com.eguller.hntoplinks.models.Statistics;
import com.eguller.hntoplinks.repository.StatisticRepository;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@ApplicationScope
public class StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Value("${hntoplinks.stats-cache-expiry}")
    private long statsCacheExpiry;

    private StatisticRepository statisticRepository;

    private Statistics statistics = null;
    private LocalDateTime lastStatisticsLoaded = LocalDateTime.MIN;

    public StatisticsService(StatisticRepository statisticRepository){
        this.statisticRepository = statisticRepository;
    }

    public Statistics readStatistics() {
        if(lastStatisticsLoaded.isAfter(LocalDateTime.now().minusSeconds(statsCacheExpiry)) && statistics!= null){
            return statistics;
        }

        var builder = Statistics.builder();
        statisticRepository.findAll().forEach(statisticEntity -> {
            if (StatKey.SUBSCRIBERS.name().equals(statisticEntity.getStatKey())) {
                builder.subscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.ACTIVE_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.activeSubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.DAILY_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.dailySubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.WEEKLY_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.weeklySubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.MONTHLY_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.monthlySubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.ANNUALLY_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.annuallySubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.UNSUBSCRIBES.name().equals(statisticEntity.getStatKey())) {
                builder.unsubscribeCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.SUCCESS_EMAIL_COUNT.name().equals(statisticEntity.getStatKey())) {
                builder.successEmailCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.FAILURE_EMAIL_COUNT.name().equals(statisticEntity.getStatKey())) {
                builder.failureEmailCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.LAST_EMAIL_SENT.name().equals(statisticEntity.getStatKey())) {
                LocalDateTime emailSentTime = LocalDateTime.parse(statisticEntity.getStatValue(), DATE_TIME_FORMATTER);
                builder.lastEmailSent(emailSentTime);
            } else if (StatKey.LAST_HN_UPDATE.name().equals(statisticEntity.getStatKey())) {
                LocalDateTime lastHnUpdateTime = LocalDateTime.parse(statisticEntity.getStatValue(), DATE_TIME_FORMATTER);
                builder.lastHnUpdate(lastHnUpdateTime);
            } else {
                logger.warn("Unknown stat key {}", statisticEntity.getStatKey());
            }
        });

        statistics = builder.build();
        lastStatisticsLoaded = LocalDateTime.now();
        return statistics;
    }

    public void updateLastHnUpdate(){
        statisticRepository.updateStatistic(StatKey.LAST_HN_UPDATE, DATE_TIME_FORMATTER.format(LocalDateTime.now()));
    }
}
