package jobs;

import cache.CacheUnit;
import cache.ItemCache;
import models.Item;
import models.Subscription;

import java.util.Calendar;
import java.util.List;

/**
 * User: eguller
 * Date: 4/6/14
 * Time: 11:27 PM
 */
public class AnnuallyMailList extends EmailList{
    int year;
    AnnuallyMailList(){
        year = Calendar.getInstance().get(Calendar.YEAR);
    }
    @Override
    public void send() {
        List<Subscription> subscriptionList = Subscription.annualSubscribers(year);
        List<Item> itemList = ItemCache.getInstance().get(CacheUnit.year);
        sendEmail(subscriptionList, itemList, subject());
    }

    @Override
    public String subject() {
        return String.format("%d - Hacker News Top Links", year);
    }
}
