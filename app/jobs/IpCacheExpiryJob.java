package jobs;

import cache.IPCache;
import play.jobs.Every;
import play.jobs.Job;

/**
 * User: eguller
 * Date: 4/18/14
 * Time: 11:46 PM
 */
@Every("10mn")
public class IpCacheExpiryJob extends Job {
  @Override
  public void doJob() {
    IPCache.getInstance().deleteExpired();
  }
}
