package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: eguller
 * Date: 5/1/14
 * Time: 5:45 PM
 */
public class StatisticsMgr {
    private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        }
    };
    private final AtomicLong subscriberCount = new AtomicLong(0);
    private final AtomicLong unsubscribeCount = new AtomicLong(0);
    private final AtomicLong activeSubscriberCount = new AtomicLong(0);
    private final AtomicLong dailySubscriberCount = new AtomicLong(0);
    private final AtomicLong weeklySubscriberCount = new AtomicLong(0);
    private final AtomicLong  monthlySubscriberCount = new AtomicLong(0);
    private final AtomicLong annuallySubscriberCount = new AtomicLong(0);
    private final AtomicLong successEmailCount = new AtomicLong(0);
    private final AtomicLong failureEmailCount = new AtomicLong(0);
    private  long lastEmailSent = 0;
    private  long lastHnUpdate = 0;
    private boolean loaded = false;
    private StatisticsMgr(){

    }

    public void load(){
        Map<StatKey, Statistic> statisticsMap = Statistic.getAllStatMap();

        Statistic statistic = statisticsMap.get(StatKey.UNSUBSCRIBES);
        if(statistic != null){
            unsubscribeCount.set(Long.parseLong(statistic.getValue()));
        }
        subscriberCount.set(Subscription.getSubscriberCount());
        activeSubscriberCount.set(Subscription.getActiveSubscriberCount());
        dailySubscriberCount.set(Subscription.getDailySubscriberCount());
        weeklySubscriberCount.set(Subscription.getWeeklySubscriberCount());
        monthlySubscriberCount.set(Subscription.getMonthlySubscriberCount());
        annuallySubscriberCount.set(Subscription.getAnnuallySubscriberCount());
        statistic = statisticsMap.get(StatKey.SUCCESS_EMAIL_COUNT);
        if(statistic != null){
            successEmailCount.set(Long.parseLong(statistic.getValue()));
        }
        statistic = statisticsMap.get(StatKey.FAILURE_EMAIL_COUNT);
        if(statistic != null){
            failureEmailCount.set(Long.parseLong(statistic.getValue()));
        }
        loaded = true;
    }

    public boolean isLoaded(){
        return loaded;
    }

    public void incrementSubscriberCount(){
        subscriberCount.incrementAndGet();
    }

    public void incrementUnsubscribeCount(){
        unsubscribeCount.incrementAndGet();
    }

    public void incrementActiveSubscriberCount(){
        activeSubscriberCount.incrementAndGet();
    }

    public void incrementDailySubscribeCount(){
        dailySubscriberCount.incrementAndGet();
    }

    public void incrementWeeklySubscriberCount(){
        weeklySubscriberCount.incrementAndGet();
    }

    public void incrementMonthlySubscriberCount(){
        monthlySubscriberCount.incrementAndGet();
    }

    public void incrementAnnuallySubscriberCount(){
        annuallySubscriberCount.incrementAndGet();
    }

    public void incrementSuccessMailCount(){
        successEmailCount.incrementAndGet();
    }

    public void incrementFailedMailCount(){
        failureEmailCount.incrementAndGet();
    }

    public void updateLastEmailSentTime(){
        lastEmailSent = System.currentTimeMillis();
    }

    public void updateLastHnUpdateTime(){
        lastHnUpdate = System.currentTimeMillis();
    }

    private String formatDate(long timeStamp){
        Date date = new Date(timeStamp);
        return dateFormat.get().format(date);
    }

    public static StatisticsMgr instance(){
        return LazyStatisticsMgr.INSTANCE;
    }
    private static class LazyStatisticsMgr{
        public static final StatisticsMgr INSTANCE = new StatisticsMgr();
    }

    public List<Statistic> getSnapshot(){
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
}
