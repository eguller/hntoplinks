package jobs;

import models.Item;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import sun.util.resources.CalendarData;

import java.util.Calendar;
import java.util.List;

/**
 * User: eguller
 * Date: 11/16/13
 * Time: 12:05 AM
 */

@Every("12h")
public class DbTruncate extends Job {
    private static final int MAX_ITEM_COUNT = 6000;
    @Override
    public void doJob() {
        int diff = (int)(Item.count() - MAX_ITEM_COUNT);

        if(diff > 0){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            List<Item> expiredItems = Item.find("select item from Item item where date < ? order by points asc", cal.getTime()).fetch(diff);
            for(Item item : expiredItems){
                item.delete();
            }
            Logger.info("Diff is %s, %s elements was deleted", diff, expiredItems.size());
        }
        else {
            Logger.info("Diff is %s deleting elements was skipped", diff);
        }
    }
}
