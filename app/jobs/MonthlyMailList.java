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
public class MonthlyMailList extends EmailList{
    private static final int ITEM_SIZE = 100;
    int month;
    public MonthlyMailList(){
        this.month = Calendar.getInstance().get(Calendar.MONTH);
    }
    @Override
    public void send() {
        List<Subscription> subscriptionList = Subscription.monthlySubscribers(month);
        List<Item> itemList = getItems();
        if(!(itemList.size() < ItemCache.ITEM_PER_PAGE)) {
            sendEmail(subscriptionList, itemList, subject());
        }
    }

    @Override
    public String subject() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -15);
        DateFormat lastMonth = new SimpleDateFormat("MMMM yyyy");
        String lmString = lastMonth.format(calendar.getTime());
        return lmString + " - Best of Last Month";
    }

    @Override
    public int postCount() {
        return ITEM_SIZE;
    }

    @Override
    public CacheUnit cacheUnit() {
        return CacheUnit.month;
    }

    @Override
    public void sendEmail(List<Subscription> subscriptions, List<Item> itemList, String subject){
        for(Subscription subscription : subscriptions){
            try {
                sendEmail(subscription, itemList, subject);
                JPAPlugin.startTx(false);
                subscription.setMonth(month);
                subscription.save();
                JPAPlugin.closeTx(false);
            } catch (EmailException e) {
                e.printStackTrace();
            }
        }
    }
}
