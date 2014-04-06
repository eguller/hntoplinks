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
 * Time: 11:25 PM
 */
public class DailyMailList extends EmailList{
    @Override
    public void send() {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        List<Subscription> subscriptionList = Subscription.dailySubscribers(today);
        List<Item> itemList = ItemCache.getInstance().get(CacheUnit.today);
        sendEmail(subscriptionList, itemList, subject());
    }

    @Override
    public String subject() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        DateFormat yesterday = new SimpleDateFormat("EEEE, dd MMMM");
        String timePrefix =  yesterday.format(calendar.getTime());
        return timePrefix + " - Hacker News Top Links";
    }
}
