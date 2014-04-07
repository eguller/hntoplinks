package cache;

/**
 * User: eguller
 * Date: 4/7/14
 * Time: 10:22 PM
 */
public class ConfigCacheItem {
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isOverridePlayConfig() {
        return overridePlayConfig;
    }

    public ConfigCacheItem(String key, String value, boolean overridePlayConfig){
        this.key = key;
        this.value = value;
        this.overridePlayConfig = overridePlayConfig;
    }

    String key;
    String value;
    boolean overridePlayConfig;
}
