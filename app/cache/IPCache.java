package cache;

import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * User: eguller
 * Date: 4/18/14
 * Time: 10:21 PM
 */
public class IPCache {
  private static final int               CACHE_EXPIRE_MIN = 10;
  private static final Map<String, Long> ipMap            = new ConcurrentHashMap<String, Long>();

  public static IPCache getInstance() {
    return IPCacheLoader.INSTANCE;
  }

  public void addIp(String ip) {
    ipMap.put(ip, System.currentTimeMillis());
  }

  public boolean checkRequired(String ip) {
    Long time = ipMap.get(ip);
    if (time == null) {
      return false;
    } else {
      long diff = System.currentTimeMillis() - time;
      long diffMin = TimeUnit.MILLISECONDS.toMinutes(diff);
      return diffMin < CACHE_EXPIRE_MIN;
    }
  }

  public void deleteExpired() {
    long current = System.currentTimeMillis();
    List<String> key2Remove = new ArrayList<String>();
    for (String key : ipMap.keySet()) {
      long diff = current - ipMap.get(key);
      long diffMin = TimeUnit.MILLISECONDS.toMinutes(diff);
      if (diffMin > CACHE_EXPIRE_MIN) {
        key2Remove.add(key);
      }
    }

    for (String key : key2Remove) {
      ipMap.remove(key);
    }

    Logger.info("%d ip addresses expired.", key2Remove.size());
  }

  private static class IPCacheLoader {
    private static final IPCache INSTANCE = new IPCache();
  }
}
