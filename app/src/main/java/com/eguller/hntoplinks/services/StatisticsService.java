package com.eguller.hntoplinks.services;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import com.eguller.hntoplinks.entities.StatisticEntity;
import com.eguller.hntoplinks.models.StatKey;
import com.eguller.hntoplinks.models.Statistics;
import com.eguller.hntoplinks.repository.StatisticRepository;

@Service
@ApplicationScope
public class StatisticsService {
  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

  @Value("${hntoplinks.stats-cache-expiry}")
  private long statsCacheExpiry;

  private final StatisticRepository statisticRepository;

  private Statistics statistics = null;
  private LocalDateTime lastStatisticsLoaded = LocalDateTime.MIN;

  private final AtomicInteger sendEmailFailed = new AtomicInteger(0);
  private final AtomicInteger sendEmailSuccess = new AtomicInteger(0);
  private final AtomicInteger userSubscribed = new AtomicInteger(0);
  private final AtomicInteger userUnsubcribed = new AtomicInteger(0);
  private final AtomicInteger dailySubscribers = new AtomicInteger(0);
  private final AtomicInteger weeklySubscribers = new AtomicInteger(0);
  private final AtomicInteger monthlySubscribers = new AtomicInteger(0);
  private final AtomicInteger annuallySubscribers = new AtomicInteger(0);

  private LocalDateTime lastEmailSend = null;

  public StatisticsService(StatisticRepository statisticRepository) {
    this.statisticRepository = statisticRepository;
  }

  public Statistics readStatistics() {
    if (lastStatisticsLoaded.isAfter(LocalDateTime.now().minusSeconds(statsCacheExpiry))
        && statistics != null) {
      return statistics;
    }

    var builder = Statistics.builder();
    statisticRepository
        .findAll()
        .forEach(
            statisticEntity -> {
              if (StatKey.SUBSCRIBERS.name().equals(statisticEntity.getStatKey())) {
                builder.subscriberCount(Long.parseLong(statisticEntity.getStatValue()));
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
                LocalDateTime emailSentTime =
                    LocalDateTime.parse(statisticEntity.getStatValue(), DATE_TIME_FORMATTER);
                builder.lastEmailSent(emailSentTime);
              } else if (StatKey.LAST_HN_UPDATE.name().equals(statisticEntity.getStatKey())) {
                LocalDateTime lastHnUpdateTime =
                    LocalDateTime.parse(statisticEntity.getStatValue(), DATE_TIME_FORMATTER);
                builder.lastHnUpdate(lastHnUpdateTime);
              } else {
                logger.warn("Unknown stat key {}", statisticEntity.getStatKey());
              }
            });

    statistics = builder.build();
    lastStatisticsLoaded = LocalDateTime.now();
    return statistics;
  }

  public void updateLastHnUpdate() {
    statisticRepository.updateStatistic(
        StatKey.LAST_HN_UPDATE, DATE_TIME_FORMATTER.format(LocalDateTime.now()));
  }

  public void sendEmailFailed() {
    sendEmailFailed.incrementAndGet();
  }

  public void sendEmailSuccess() {
    sendEmailSuccess.incrementAndGet();
    this.lastEmailSend = LocalDateTime.now();
  }

  public void userSubscribed() {
    userSubscribed.incrementAndGet();
  }

  public void userUnsubscribed() {
    userUnsubcribed.incrementAndGet();
  }

  public void userSubscribedForDaily() {
    dailySubscribers.incrementAndGet();
  }

  public void userSubscribedForWeekly() {
    weeklySubscribers.incrementAndGet();
  }

  public void userSubscribedForMonthly() {
    monthlySubscribers.incrementAndGet();
  }

  public void userSubscribedForAnnually() {
    annuallySubscribers.incrementAndGet();
  }

  public void updateStatistics() {
    int sendEmailFailed = this.sendEmailFailed.getAndSet(0);
    int sendEmailSuccess = this.sendEmailSuccess.getAndSet(0);
    int usersSubscribed = this.userSubscribed.getAndSet(0);
    int usersUnsubscribed = this.userUnsubcribed.getAndSet(0);
    int dailySubscribers = this.dailySubscribers.getAndSet(0);
    int weeklySubscribers = this.weeklySubscribers.getAndSet(0);
    int monthlySubscribers = this.monthlySubscribers.getAndSet(0);
    int annuallySubscribers = this.annuallySubscribers.getAndSet(0);

    Map<String, StatisticEntity> statisticsMap =
        StreamSupport.stream(statisticRepository.findAll().spliterator(), false)
            .collect(Collectors.toMap(StatisticEntity::getStatKey, Function.identity()));

    var updatedStatistics = new ArrayList<StatisticEntity>();

    var sendEmailFailedStatEntity =
        updatedStatistics(StatKey.FAILURE_EMAIL_COUNT.name(), statisticsMap, sendEmailFailed);
    updatedStatistics.add(sendEmailFailedStatEntity);

    var sendEmailSuccessEntity =
        updatedStatistics(StatKey.SUCCESS_EMAIL_COUNT.name(), statisticsMap, sendEmailSuccess);
    updatedStatistics.add(sendEmailSuccessEntity);

    var subscribersEntity =
        updatedStatistics(StatKey.SUBSCRIBERS.name(), statisticsMap, usersSubscribed);
    updatedStatistics.add(subscribersEntity);

    var unsubscribesEntity =
        updatedStatistics(StatKey.UNSUBSCRIBES.name(), statisticsMap, usersUnsubscribed);
    updatedStatistics.add(unsubscribesEntity);

    var dailySubscribersEntity =
        updatedStatistics(StatKey.ANNUALLY_SUBSCRIBER.name(), statisticsMap, dailySubscribers);
    updatedStatistics.add(dailySubscribersEntity);

    var weeklySubscribersEntity =
        updatedStatistics(StatKey.WEEKLY_SUBSCRIBER.name(), statisticsMap, weeklySubscribers);
    updatedStatistics.add(weeklySubscribersEntity);

    var monthlySubscribersEntity =
        updatedStatistics(StatKey.MONTHLY_SUBSCRIBER.name(), statisticsMap, monthlySubscribers);
    updatedStatistics.add(monthlySubscribersEntity);

    var annuallySubscribersEntity =
        updatedStatistics(StatKey.ANNUALLY_SUBSCRIBER.name(), statisticsMap, annuallySubscribers);
    updatedStatistics.add(annuallySubscribersEntity);

    if (lastEmailSend != null) {
      var lastEmailSentEntity = statisticsMap.get(StatKey.LAST_EMAIL_SENT.name());
      if (lastEmailSentEntity == null) {
        lastEmailSentEntity = new StatisticEntity();
        lastEmailSentEntity.setStatKey(StatKey.LAST_EMAIL_SENT.name());
        lastEmailSentEntity.setStatValue(DATE_TIME_FORMATTER.format(lastEmailSend));
      } else {
        lastEmailSentEntity.setStatValue(DATE_TIME_FORMATTER.format(lastEmailSend));
      }
      updatedStatistics.add(lastEmailSentEntity);
    }

    statisticRepository.saveAll(updatedStatistics);
  }

  private StatisticEntity updatedStatistics(
      String statKey, Map<String, StatisticEntity> statisticsMap, int updateValue) {
    return createOrUpdateStatisticsEntity(statKey, statisticsMap.get(statKey), updateValue);
  }

  private StatisticEntity createOrUpdateStatisticsEntity(
      String statKey, StatisticEntity existingEntity, int updateValue) {
    if (existingEntity == null) {
      var statisticsEntity = new StatisticEntity();
      statisticsEntity.setStatKey(statKey);
      statisticsEntity.setStatValue(String.valueOf(updateValue));
      return statisticsEntity;
    } else {
      var newValue = Integer.parseInt(existingEntity.getStatValue()) + updateValue;
      existingEntity.setStatValue(String.valueOf(newValue));
      return existingEntity;
    }
  }
}
