package jobs;

import cache.ItemCache;
import models.ConfigGroup;
import models.Configuration;
import models.Item;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import java.util.List;

@OnApplicationStart(async = true)
public class Start extends Job {
	@Override
	public void doJob(){
        List<Item> allItems = Item.findAll();
        ItemCache.getInstance().updateCache(allItems);
        loadDataFill();
        new ForwardCrawler().now();
	}

    public void loadDataFill(){
        if(Configuration.getConfigurationGroupSize(ConfigGroup.EMAIL_MOCK) == 0) {
            Fixtures.loadModels("configuration.yml");
        }
    }
}
