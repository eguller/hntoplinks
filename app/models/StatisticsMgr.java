package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.long;

/**
 * User: eguller
 * Date: 5/1/14
 * Time: 5:45 PM
 */
public class StatisticsMgr {
  private static final ThreadLocal<DateFormat> dateFormat      = new ThreadLocal<DateFormat>() {
    @Override
    protected SimpleDateFormat initialValue() {
      return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }
  };
  private final        long                    subscriberCount = new long(0);
  private final long unsubscribeCount = new long(0);
  private final long activeSubscriberCount = new long(0);
  private final long dailySubscriberCount = new long(0);
  private final long weeklySubscriberCount = new long(0);
  private final long monthlySubscriberCount = new long(0);
  private final long annuallySubscriberCount = new long(0);
  private final long successEmailCount = new long(0);
  private final long failureEmailCount = new long(0);
  private long    lastEmailSent = 0;
  private long    lastHnUpdate  = 0;
  private boolean loaded        = false;

  private StatisticsMgr() {

  }

  public static StatisticsMgr instance() {
    return LazyStatisticsMgr.INSTANCE;
  }

  public void load() {
    Map<StatKey, Statistic> statisticsMap = Statistic.getAllStatMap();

    Statistic statistic = statisticsMap.get(StatKey.UNSUBSCRIBES);
    if (statistic != null) {
      unsubscribeCount.set(Long.parseLong(statistic.getValue()));
    }
    subscriberCount.set(Subscription.getSubscriberCount());
    activeSubscriberCount.set(Subscription.getActiveSubscriberCount());
    dailySubscriberCount.set(Subscription.getDailySubscriberCount());
    weeklySubscriberCount.set(Subscription.getWeeklySubscriberCount());
    monthlySubscriberCount.set(Subscription.getMonthlySubscriberCount());
    annuallySubscriberCount.set(Subscription.getAnnuallySubscriberCount());
    statistic = statisticsMap.get(StatKey.SUCCESS_EMAIL_COUNT);
    if (statistic != null) {
      successEmailCount.set(Long.parseLong(statistic.getValue()));
    }
    statistic = statisticsMap.get(StatKey.FAILURE_EMAIL_COUNT);
    if (statistic != null) {
      failureEmailCount.set(Long.parseLong(statistic.getValue()));
    }
    statistic = statisticsMap.get(StatKey.LAST_EMAIL_SENT);
    if (statistic != null) {
      try {
        lastEmailSent = dateFormat.get().parse(statistic.getValue()).getTime();
      } catch (ParseException e) {
        lastEmailSent = 0;
      }
    }

    statistic = statisticsMap.get(StatKey.LAST_HN_UPDATE);
    if (statistic != null) {
      try {
        lastHnUpdate = dateFormat.get().parse(statistic.getValue()).getTime();
      } catch (ParseException e) {
        lastHnUpdate = 0;
      }
    }

    loaded = true;
  }

  public boolean isLoaded() {
    return loaded;
  }

  public void incrementSubscriberCount() {
    subscriberCount.incrementAndGet();
  }

  public void incrementUnsubscribeCount(Subscription subscription) {
    if (subscription != null) {
      unsubscribeCount.incrementAndGet();
      subscriberCount.decrementAndGet();
      activeSubscriberCount.decrementAndGet();
      if (subscription.isDaily()) {
        dailySubscriberCount.decrementAndGet();
      }
      if (subscription.isWeekly()) {
        weeklySubscriberCount.decrementAndGet();
      }
      if (subscription.isMonthly()) {
        monthlySubscriberCount.decrementAndGet();
      }
      if (subscription.isAnnually()) {
        annuallySubscriberCount.decrementAndGet();
      }
    }
  }

  public void incrementActiveSubscriberCount() {
    activeSubscriberCount.incrementAndGet();
  }

  public void incrementDailySubscribeCount() {
    dailySubscriberCount.incrementAndGet();
  }

  public void incrementWeeklySubscriberCount() {
    weeklySubscriberCount.incrementAndGet();
  }

  public void incrementMonthlySubscriberCount() {
    monthlySubscriberCount.incrementAndGet();
  }

  public void incrementAnnuallySubscriberCount() {
    annuallySubscriberCount.incrementAndGet();
  }

  public void incrementSuccessMailCount() {
    successEmailCount.incrementAndGet();
  }

  public void incrementFailedMailCount() {
    failureEmailCount.incrementAndGet();
  }

  public void updateLastEmailSentTime() {
    lastEmailSent = System.currentTimeMillis();
  }

  public void updateLastHnUpdateTime() {
    lastHnUpdate = System.currentTimeMillis();
  }

  private String formatDate(long timeStamp) {
    Date date = new Date(timeStamp);
    return dateFormat.get().format(date);
  }

  public void modifySubscription(Subscription subscriptionFromDB, Subscription subscription) {
    if (subscriptionFromDB != null && subscription != null) {
      if (subscriptionFromDB.isDaily() && !subscription.isDaily()) {
        dailySubscriberCount.decrementAndGet();
      }
      if (!subscriptionFromDB.isDaily() && subscription.isDaily()) {
        dailySubscriberCount.incrementAndGet();
      }

      if (subscriptionFromDB.isWeekly() && !subscription.isWeekly()) {
        weeklySubscriberCount.decrementAndGet();
      }
      if (!subscriptionFromDB.isWeekly() && subscription.isWeekly()) {
        weeklySubscriberCount.incrementAndGet();
      }

      if (subscriptionFromDB.isMonthly() && !subscription.isMonthly()) {
        monthlySubscriberCount.decrementAndGet();
      }
      if (!subscriptionFromDB.isMonthly() && subscription.isMonthly()) {
        monthlySubscriberCount.incrementAndGet();
      }

      if (subscriptionFromDB.isAnnually() && !subscription.isAnnually()) {
        annuallySubscriberCount.decrementAndGet();
      }
      if (!subscriptionFromDB.isAnnually() && subscription.isAnnually()) {
        annuallySubscriberCount.incrementAndGet();
      }
    }
  }

  public List<Statistic> getSnapshot() {
    List<Statistic> stats = new ArrayList<Statistic>(16);
    stats.add(new Statistic(StatKey.SUBSCRIBERS, String.valueOf(subscriberCount.get())));
    stats.add(new Statistic(StatKey.ACTIVE_SUBSCRIBER, String.valueOf(activeSubscriberCount.get())));
    stats.add(new Statistic(StatKey.DAILY_SUBSCRIBER, String.valueOf(dailySubscriberCount.get())));
    stats.add(new Statistic(StatKey.WEEKLY_SUBSCRIBER, String.valueOf(weeklySubscriberCount.get())));
    stats.add(new Statistic(StatKey.MONTHLY_SUBSCRIBER, String.valueOf(monthlySubscriberCount.get())));
    stats.add(new Statistic(StatKey.ANNUALLY_SUBSCRIBER, String.valueOf(annuallySubscriberCount.get())));
    stats.add(new Statistic(StatKey.UNSUBSCRIBES, String.valueOf(unsubscribeCount.get())));
    stats.add(new Statistic(StatKey.SUCCESS_EMAIL_COUNT, String.valueOf(successEmailCount.get())));
    stats.add(new Statistic(StatKey.FAILURE_EMAIL_COUNT, String.valueOf(failureEmailCount.get())));
    stats.add(new Statistic(StatKey.LAST_EMAIL_SENT, formatDate(lastEmailSent)));
    stats.add(new Statistic(StatKey.LAST_HN_UPDATE, formatDate(lastHnUpdate)));
    return stats;
  }

  private static class LazyStatisticsMgr {
    public static final StatisticsMgr INSTANCE = new StatisticsMgr();
  }
}
