package email;

import cache.CacheUnit;
import models.Item;
import models.Subscription;
import play.db.jpa.JPA;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * User: eguller Date: 4/6/14 Time: 11:26 PM
 */
public class MonthlyMailList extends EmailList {
  private static final int ITEM_SIZE = 100;

  public MonthlyMailList() {

  }

  @Override
  public void send() {
    List<Subscription> subscriptionList = Subscription.monthlySubscribers();
    if (subscriptionList.size() > 0) {
      List<Item> itemList = getItems();
      if (itemList.size() > 0) {
        sendEmail(subscriptionList, itemList);
      }
    }
  }

  @Override
  public String subject(Subscription subscription) {
    Calendar calendar = subscription.getSendMonthInOwnTimeZone();
    calendar.add(Calendar.DAY_OF_YEAR, -15);
    DateFormat lastMonth = new SimpleDateFormat("MMMM yyyy");
    lastMonth.setTimeZone(subscription.getTimZoneObj());
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
  public void updateNextSendDate(Subscription subscription) {
    JPA.em().createQuery("update Subscription set nextSendMonth = :nextSendMonth where id = :id")
      .setParameter("nextSendMonth", subscription.calculateNextSendMonth())
      .setParameter("id", subscription.getId())
      .executeUpdate();

  }
}
