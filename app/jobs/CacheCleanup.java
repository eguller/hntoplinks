package jobs;

import cache.CacheUnit;
import cache.ItemCache;
import play.jobs.Every;
import play.jobs.Job;

/**
 * User: eguller
 * Date: 11/15/13
 * Time: 11:38 PM
 */

@Every("10mn")
public class CacheCleanup extends Job {

  @Override
  public void doJob() {
    ItemCache.getInstance().cleanupExpired();
  }
}
