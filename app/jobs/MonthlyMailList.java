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
public class MonthlyMailList extends EmailList{
    @Override
    public void send() {
        int month = Calendar.getInstance().get(Calendar.MONTH);
        List<Subscription> subscriptionList = Subscription.monthlySubscribers(month);
        List<Item> itemList = ItemCache.getInstance().get(CacheUnit.month);

        sendEmail(subscriptionList, itemList, subject());
    }

    @Override
    public String subject() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -15);
        DateFormat lastMonth = new SimpleDateFormat("MMMM yyyy");
        String lmString = lastMonth.format(calendar.getTime());
        return lmString + " - Hacker News Top Links";
    }
}
