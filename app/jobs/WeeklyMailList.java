package jobs;

import cache.CacheUnit;
import cache.ItemCache;
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
    int week;
    public WeeklyMailList(){
        week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }
    @Override
    public void send() {
        List<Subscription> subscriptionList = Subscription.weeklySubscribers(week);
        List<Item> itemList = getItems();
        if(!(itemList.size() < ItemCache.ITEM_PER_PAGE)) {
            sendEmail(subscriptionList, itemList, subject());
        }
    }

    @Override
    public String subject() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        Calendar calendar = Calendar.getInstance();
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
    public void sendEmail(List<Subscription> subscriptions, List<Item> itemList, String subject){
        for(Subscription subscription : subscriptions){
            try {
                sendEmail(subscription, itemList, subject);
                JPAPlugin.startTx(false);
                subscription.setWeek(week);
                subscription.save();
                JPAPlugin.closeTx(false);
            } catch (EmailException e) {
                e.printStackTrace();
            }
        }
    }
}
