package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.StatKey;
import com.eguller.hntoplinks.models.Statistics;
import com.eguller.hntoplinks.repository.StatisticRepository;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    @Autowired
    private StatisticRepository statisticRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Statistics readStatistics() {
        Statistics.Builder builder = new Statistics.Builder();
        statisticRepository.findAll().forEach(statisticEntity -> {
            if (StatKey.SUBSCRIBERS.name().equals(statisticEntity.getStatKey())) {
                builder.setSubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.ACTIVE_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.setActiveSubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.DAILY_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.setDailySubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.WEEKLY_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.setWeeklySubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.MONTHLY_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.setMonthlySubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.ANNUALLY_SUBSCRIBER.name().equals(statisticEntity.getStatKey())) {
                builder.setAnnuallySubscriberCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.UNSUBSCRIBES.name().equals(statisticEntity.getStatKey())) {
                builder.setUnsubscribeCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.SUCCESS_EMAIL_COUNT.name().equals(statisticEntity.getStatKey())) {
                builder.setSuccessEmailCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.FAILURE_EMAIL_COUNT.name().equals(statisticEntity.getStatKey())) {
                builder.setFailureEmailCount(Long.parseLong(statisticEntity.getStatValue()));
            } else if (StatKey.LAST_EMAIL_SENT.name().equals(statisticEntity.getStatKey())) {
                LocalDateTime emailSentTime = LocalDateTime.parse(statisticEntity.getStatValue(), DATE_TIME_FORMATTER);
                builder.setLastEmailSent(emailSentTime);
            } else if (StatKey.LAST_HN_UPDATE.name().equals(statisticEntity.getStatKey())) {
                LocalDateTime lastHnUpdateTime = LocalDateTime.parse(statisticEntity.getStatValue(), DATE_TIME_FORMATTER);
                builder.setLastEmailSent(lastHnUpdateTime);
            } else {
                logger.warn("Unknown stat key {}", statisticEntity.getStatKey());
            }
        });
        long dailysSubscribersCount = subscriptionRepository.countByDailyIsTrue();
        long weeklySubscribersCount = subscriptionRepository.countByWeeklyIsTrue();
        long monthlySubscribersCount = subscriptionRepository.countByMonthlyIsTrue();
        long annuallySubscribersCount = subscriptionRepository.countByAnnuallyIsTrue();

        builder.setDailySubscriberCount(dailysSubscribersCount);
        builder.setWeeklySubscriberCount(weeklySubscribersCount);
        builder.setMonthlySubscriberCount(monthlySubscribersCount);
        builder.setAnnuallySubscriberCount(annuallySubscribersCount);
        Statistics statistics = builder.createStatistics();
        return statistics;
    }
}
