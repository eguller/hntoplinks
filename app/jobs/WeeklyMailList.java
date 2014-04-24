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
 * Time: 11:26 PM
 */
public class WeeklyMailList extends EmailList{
    private static final int ITEM_SIZE = 50;
    public WeeklyMailList(){
        
    }
    @Override
    public void send() {
        List<Subscription> subscriptionList = Subscription.weeklySubscribers();
        List<Item> itemList = getItems();
        if(itemList.size() > 0) {
            sendEmail(subscriptionList, itemList);
        }
    }

    @Override
    public String subject(Subscription subscription) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        dateFormat.setTimeZone(subscription.getTimZoneObj());
        Calendar calendar = subscription.getSendWeekInOwnTimeZone();
        calendar.add(Calendar.DATE, -1);
        String toDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, -7);
        String fromDate = dateFormat.format(calendar.getTime());
        return String.format("%s - %s Weekly Top Links", fromDate, toDate);
    }

    @Override
    public int postCount() {
        return ITEM_SIZE;
    }

    @Override
    public CacheUnit cacheUnit() {
        return CacheUnit.week;
    }

	@Override
	public void updateNextSendDate(Subscription subscription) {
		subscription.updateNextSendWeek();
	}
}
