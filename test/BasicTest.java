import cache.CacheUnit;
import cache.ItemCache;
import org.junit.*;

import java.util.*;

import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

  @Test
  public void itemEqualityTest() {
    Item item1 = new Item("Unique Item Test", "http://hntoplinks.com", "hntoplinks.com", "eguller", Calendar.getInstance().getTime(), 1, 1, 1);
    Item item2 = new Item("Unique Item Test", "http://hntoplinks.com", "hntoplinks.com", "eguller", Calendar.getInstance().getTime(), 1, 2, 2);
    assertTrue(item1.equals(item2));
  }

  @Test
  public void itemUniqueInSetTest() {
    Item item1 = new Item("Unique Item Test", "http://hntoplinks.com", "hntoplinks.com", "eguller", Calendar.getInstance().getTime(), 1, 1, 1);
    Item item2 = new Item("Unique Item Test", "http://hntoplinks.com", "hntoplinks.com", "eguller", Calendar.getInstance().getTime(), 1, 2, 2);
    TreeSet<Item> itemTreeSet = new TreeSet<Item>();
    itemTreeSet.add(item1);
    itemTreeSet.add(item2);
    assertEquals(1, itemTreeSet.size());
  }

  @Test
  public void itemUpdatedTest() {
    Item item1 = new Item("Unique Item Test", "http://hntoplinks.com", "hntoplinks.com", "eguller", Calendar.getInstance().getTime(), 1, 1, 1);
    Item item2 = new Item("Unique Item Test", "http://hntoplinks.com", "hntoplinks.com", "eguller", Calendar.getInstance().getTime(), 1, 2, 2);
    TreeSet<Item> itemTreeSet = new TreeSet<Item>();
    itemTreeSet.add(item1);
    itemTreeSet.remove(item2);
    itemTreeSet.add(item2);
    Item item = itemTreeSet.first();
    assertEquals(2, item.points);
  }

}
