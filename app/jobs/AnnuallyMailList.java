package jobs;

import cache.CacheUnit;
import cache.ItemCache;
import models.Item;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;

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
        if(!(itemList.size() < ItemCache.ITEM_PER_PAGE)) {
            sendEmail(subscriptionList, itemList, subject());
        }
    }

    @Override
    public String subject() {
        return String.format("Best of %d", year);
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
