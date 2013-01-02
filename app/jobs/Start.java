package jobs;

import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Start extends Job {
	@Override
	public void doJob(){
		new ForwardCrawler().now();
	}
}
