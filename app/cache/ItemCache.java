package cache;

import models.Item;
import play.Logger;

import java.util.*;

/**
 * User: eguller
 * Date: 11/8/13
 * Time: 10:58 PM
 */
public class ItemCache {
    private static final ItemCache INSTANCE = new ItemCache();
    private static final int ITEM_PER_PAGE = 30;
    Map<CacheUnit, ItemCacheUnit> cacheMap = new HashMap<CacheUnit, ItemCacheUnit>();

    private ItemCache() {
        cacheMap.put(CacheUnit.today, new ItemCacheUnit(1));
        cacheMap.put(CacheUnit.week, new ItemCacheUnit(7));
        cacheMap.put(CacheUnit.month, new ItemCacheUnit(30));
        cacheMap.put(CacheUnit.year, new ItemCacheUnit(365));
        cacheMap.put(CacheUnit.all, new ItemCacheUnit(Integer.MAX_VALUE));
    }

    public static ItemCache getInstance() {
        return INSTANCE;
    }

    public void updateCache(List<Item> items) {
        for (ItemCacheUnit cacheUnit : cacheMap.values()) {
            cacheUnit.addNewItems(items);
        }
    }

    public List<Item> get(CacheUnit unit, int page) {
        ItemCacheUnit cacheUnit = cacheMap.get(unit);
        return cacheUnit.getItemsForPage(page);
    }


    public void cleanupExpired() {
        for (ItemCacheUnit icu : cacheMap.values()) {
            icu.cleanupExpired();
        }
    }

    public static class ItemCacheUnit {
        private static final int DEFAULT_MAX_SIZE = 300;
        List<Item> itemList = new ArrayList<Item>();
        int daysOld;
        int maxSize = DEFAULT_MAX_SIZE;

        ItemCacheUnit(int daysOld) {
            this.daysOld = daysOld;
        }

        ItemCacheUnit(int daysOld, int maxSize) {
            this(daysOld);
            this.maxSize = maxSize;
        }

        public void addNewItems(List<Item> itemList) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -daysOld);
            for (Item item : itemList) {
                if (item.date.after(calendar.getTime())) {
                    if (!this.itemList.contains(item)) {
                        this.itemList.add(item);
                    }
                }
            }
            Collections.sort(this.itemList);
            for (; this.itemList.size() > maxSize; this.itemList.remove(this.itemList.size() - 1));

            Logger.info("%s new items added to item cache.", itemList.size());
        }

        public List<Item> getItemsForPage(int page) {
            int start = Math.min((page - 1) * ITEM_PER_PAGE, itemList.size());
            int end = Math.min(start + ITEM_PER_PAGE, itemList.size());
            return itemList.subList(start, end);
        }

        public void cleanupExpired() {
            List<Item> expiredItems = new ArrayList<Item>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -daysOld);
            for (Item item : itemList) {
                if (item.date.before(calendar.getTime())) {
                    expiredItems.add(item);
                }
            }

            itemList.removeAll(expiredItems);
            Logger.info("%s items expired in cache and cleaned up", expiredItems.size());
        }
    }

}
