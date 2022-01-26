package email;

import cache.CacheUnit;
import models.Item;
import models.Subscription;
import play.db.jpa.JPA;

import java.util.Calendar;
import java.util.List;

/**
 * User: eguller Date: 4/6/14 Time: 11:27 PM
 */
public class AnnuallyMailList extends EmailList {
  private static final int ITEM_SIZE = 200;

  AnnuallyMailList() {
  }

  @Override
  public void send() {
    List<Subscription> subscriptionList = Subscription.annualSubscribers();
    if (subscriptionList.size() > 0) {
      List<Item> itemList = getItems();
      if (itemList.size() > 0) {
        sendEmail(subscriptionList, itemList);
      }
    }
  }

  @Override
  public String subject(Subscription subscription) {
    Calendar calendar = subscription.getSendYearInOwnTimeZone();
    calendar.add(Calendar.YEAR, -1);
    int year = calendar.get(Calendar.YEAR);
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
  public void updateNextSendDate(Subscription subscription) {
    JPA.em().createQuery("update Subscription set nextSendYear = :nextSendYear where id = :id")
      .setParameter("nextSendYear", subscription.calculateNextSendYear())
      .setParameter("id", subscription.getId())
      .executeUpdate();

  }
}
