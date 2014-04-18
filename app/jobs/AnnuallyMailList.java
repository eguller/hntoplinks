package jobs;

import cache.CacheUnit;
import models.Item;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import play.db.jpa.JPAPlugin;

import java.util.Calendar;
import java.util.List;

/**
 * User: eguller
 * Date: 4/6/14
 * Time: 11:27 PM
 */
public class AnnuallyMailList extends EmailList{
    private static final int ITEM_SIZE = 200;
    int year;
    AnnuallyMailList(){
        year = Calendar.getInstance().get(Calendar.YEAR);
    }
    @Override
    public void send() {
        List<Subscription> subscriptionList = Subscription.annualSubscribers(year);
        List<Item> itemList = getItems();
        if(itemList.size() > 0) {
            sendEmail(subscriptionList, itemList, subject());
        }
    }

    @Override
    public String subject() {
        return String.format("Best of %d", year);
    }

    @Override
    public int postCount() {
        return ITEM_SIZE;
    }

    @Override
    public CacheUnit cacheUnit() {
        return CacheUnit.year;
    }

    @Override
    public void sendEmail(List<Subscription> subscriptions, List<Item> itemList, String subject) {
        for(Subscription subscription : subscriptions){
            try {
                sendEmail(subscription, itemList, subject);
                JPAPlugin.startTx(false);
                subscription.setYear(year);
                subscription.save();
                JPAPlugin.closeTx(false);
            } catch (EmailException e) {
                e.printStackTrace();
            }
        }
    }
}
