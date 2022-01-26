package cache;

/**
 * User: eguller
 * Date: 4/7/14
 * Time: 10:22 PM
 */
public class ConfigCacheItem {
  String  key;
  String  value;
  boolean overridePlayConfig;

  public ConfigCacheItem(String key, String value, boolean overridePlayConfig) {
    this.key                = key;
    this.value              = value;
    this.overridePlayConfig = overridePlayConfig;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public boolean isOverridePlayConfig() {
    return overridePlayConfig;
  }
}
