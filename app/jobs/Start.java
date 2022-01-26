package jobs;

import cache.ConfigCache;
import cache.ItemCache;
import models.ConfigGroup;
import models.Configuration;
import models.Item;
import models.StatisticsMgr;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import java.util.List;

@OnApplicationStart(async = true)
public class Start extends Job {
  Job crawlerJob = new JSONDataRetriever();

  @Override
  public void doJob() {
    loadDataFill();
    ConfigCache.instance().load();
    StatisticsMgr.instance().load();
    List<Item> allItems = Item.findAll();
    ItemCache.getInstance().updateCache(allItems);
    crawlerJob.now();
  }

  public void loadDataFill() {
    if (Configuration.getConfigurationGroupSize(ConfigGroup.EMAIL_CONFIG) == 0) {
      Fixtures.loadModels("configuration.yml");
    }
  }
}
