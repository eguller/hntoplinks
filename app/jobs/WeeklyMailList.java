package jobs;

import cache.CacheUnit;
import cache.ItemCache;
import models.Item;
import models.Subscription;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * User: eguller
 * Date: 4/6/14
 * Time: 11:26 PM
 */
public class WeeklyMailList extends EmailList{
    @Override
    public void send() {
        int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        List<Subscription> subscriptionList = Subscription.weeklySubscribers(week);
        List<Item> itemList = ItemCache.getInstance().get(CacheUnit.week);
        sendEmail(subscriptionList, itemList, subject());
    }

    @Override
    public String subject() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String toDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, -7);
        String fromDate = dateFormat.format(calendar.getTime());
        return String.format("%s - %s Hacker News Top Links", fromDate, toDate);
    }
}
