package cache;

import models.Configuration;
import play.Play;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: eguller
 * Date: 4/7/14
 * Time: 10:22 PM
 */
public class ConfigCache {
    private static final ConcurrentHashMap<String, ConfigCacheItem> configCache = new ConcurrentHashMap<String, ConfigCacheItem>();

    public static ConfigCache instance(){
        return ConfigCacheLoader.INSTANCE;
    }

    public String getValue(String key){
        ConfigCacheItem item = configCache.get(key);
        if(item != null){
            return item.getValue();
        } else {
            return null;
        }
    }

    public ConfigCacheItem getItem(String key){
        return configCache.get(key);
    }

    public void load(){
        List<Configuration> configurationList = Configuration.findAll();
        for(Configuration configuration : configurationList){
            ConfigCacheItem item = new ConfigCacheItem(configuration.getKey(), configuration.getValue(), configuration.isOverridePlayConfig());
            configCache.put(configuration.getKey(), item);
            if(configuration.isOverridePlayConfig()){
                Play.configuration.setProperty(configuration.getKey(), configuration.getValue());
            }
        }
    }

    private static class ConfigCacheLoader{
        public static final ConfigCache INSTANCE = new ConfigCache();
    }
}
