package jobs;

import cache.ItemCache;
import com.google.gson.Gson;
import models.Item;
import models.JSonItem;
import models.StatisticsMgr;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: eguller
 * Date: 10/12/14
 * Time: 3:55 PM
 */
@Every("5min")
public class JSONDataRetriever extends Job {
  private static final String apiVersion = "0";
  private static final Gson   gson       = new Gson();
  private static       int    TIMEOUT    = (int) TimeUnit.SECONDS.toMillis(30);

  @Override
  public void doJob() {
    readAndConvertItems();
  }

  public void readAndConvertItems() {
    Long[] topItemIds = getTopItemIds();
    List<Item> newItemList = new ArrayList<Item>();
    UpdateStat stat = UpdateStat.start();
    for (Long id : topItemIds) {
      try {
        JSonItem jSonItem = deserializeItem(id);
        if (jSonItem == null) {
          Logger.error("Json item could not be desrialized %d", id);
          continue;
        }
        Item item = new Item(jSonItem);
        Item itemFromDb = Item.getByHnId(jSonItem.getId());
        if (itemFromDb == null) {
          item.save();
          newItemList.add(item.clone());
          stat.newItemAdded();
        } else {
          itemFromDb.update(item);
          itemFromDb.save();
          newItemList.add(itemFromDb.clone());
          stat.itemUpdated();
        }
      } catch (IOException e) {
        Logger.error(e, "Failed to save item %d", id);
        stat.itemFailed();
      }
    }
    ItemCache.getInstance().updateCache(newItemList);
    StatisticsMgr.instance().updateLastHnUpdateTime();
    Logger.info("Last update : %s. New Item: %d, Updated Item: %d, Failed Item: %d", Calendar.getInstance().getTime().toString(), stat.getNewItem(), stat.getUpdatedItem(), stat.getFailedItem());
  }

  private Long[] getTopItemIds() {
    Long[] topItemIds = new Long[0];
    try {
      topItemIds = deserialize(getTopStoriesUrl(), Long[].class);
    } catch (IOException e) {
      Logger.error(e, "Failed to query top item ids");
    }
    return topItemIds;
  }

  private JSonItem deserializeItem(Long id) throws IOException {
    return deserialize(getItemUrl(id), JSonItem.class);
  }

  private <T> T deserialize(String urlStr, Class<T> clazz) throws IOException {
    URL url = new URL(urlStr);
    HttpURLConnection huc = (HttpURLConnection) url.openConnection();
    HttpURLConnection.setFollowRedirects(false);
    huc.setConnectTimeout(TIMEOUT);
    huc.connect();
    InputStream input = huc.getInputStream();
    Reader reader = new InputStreamReader(input, "UTF-8");
    return gson.fromJson(reader, clazz);
  }

  private String getTopStoriesUrl() {
    return "https://hacker-news.firebaseio.com/v" + apiVersion + "/topstories.json";
  }

  private String getItemUrl(Long itemId) {
    return "https://hacker-news.firebaseio.com/v" + apiVersion + "/item/" + itemId + ".json";
  }

  private static class UpdateStat {
    private int newItem     = 0;
    private int updatedItem = 0;
    private int failedItem  = 0;

    public static UpdateStat start() {
      return new UpdateStat();
    }

    public void newItemAdded() {
      newItem++;
    }

    public void itemUpdated() {
      updatedItem++;
    }

    public void itemFailed() {
      failedItem++;
    }

    public int getNewItem() {
      return newItem;
    }

    public int getUpdatedItem() {
      return updatedItem;
    }

    public int getFailedItem() {
      return failedItem;
    }
  }
}
