package email;

import cache.CacheUnit;
import cache.ItemCache;
import jobs.SingleEmailSendJob;
import models.Item;
import models.Subscription;
import play.Logger;
import utils.FormatUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * User: eguller Date: 4/6/14 Time: 11:23 PM
 */
public abstract class EmailList implements NextSendDateUpdater {

    public abstract void send();

    public abstract String subject(Subscription subscriotion);

    public abstract int postCount();

    public abstract CacheUnit cacheUnit();


    public List<Item> getItems() {
        return ItemCache.getInstance().getItems(cacheUnit(), postCount());
    }


    protected void sendEmail(List<Subscription> subscriptions,
                             List<Item> itemList) {
        Logger.info("Sending %d items to %d subscribers", itemList.size(),
                subscriptions.size());
        int success = 0;
        int failure = 0;
        long start = System.currentTimeMillis();
        for (Subscription subscription : subscriptions) {
            boolean result = false;
            try {
                result = new SingleEmailSendJob(subscription, itemList, subject(subscription), this).now().get();
                if (result) {
                    success++;
                } else {
                    failure++;
                }
            } catch (InterruptedException e) {
                Logger.error(e, "Sending email to %s failed.", subscription.getEmail());
                failure ++;
            } catch (ExecutionException e) {
                Logger.error(e, "Seding email to %s failed", subscription.getEmail());
                failure ++;
            }
        }
        long diff = System.currentTimeMillis() - start;
        String secs = FormatUtil.millis2Seconds(diff);
        Logger.info(
                "Sending email completed in %ssec. Success: %d, Failure: %d, ",
                secs, success, failure);
    }


}
