package jobs;

import cache.CacheUnit;
import models.Item;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import play.db.jpa.JPAPlugin;

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
    private static final int ITEM_SIZE = 30;
    public DailyMailList(){

    }
    @Override
    public void send() {
        List<Subscription> subscriptionList = Subscription.dailySubscribers();
        List<Item> itemList = getItems();
        if(itemList.size() > 0) {
            sendEmail(subscriptionList, itemList);
        }
    }

    @Override
    public String subject(Subscription subscription) {
        Calendar calendar = subscription.getSendDayInOwnTimeZone();
        calendar.add(Calendar.DATE, -1);
        DateFormat yesterday = new SimpleDateFormat("EEEE, dd MMMM");
        yesterday.setTimeZone(subscription.getTimZoneObj());
        String timePrefix =  yesterday.format(calendar.getTime());
        return timePrefix + " - Daily Top Links";
    }

    @Override
    public int postCount() {
        return ITEM_SIZE;
    }

    @Override
    public CacheUnit cacheUnit() {
        return CacheUnit.today;
    }

    @Override
	public void updateNextSendDate(Subscription subscription) {
		subscription.updateNextSendDay();
	}
}
