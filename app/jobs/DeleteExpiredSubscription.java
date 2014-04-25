package jobs;

import java.util.Calendar;

import models.Subscription;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;

@On("0 0 1 ? * *")
public class DeleteExpiredSubscription extends Job {
	private static final int EXPIRE_IN_DAYS = 7;
	@Override
	public void doJob(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -EXPIRE_IN_DAYS);
		int deleted = Subscription.delete("subscriptionDate < ? and activated = ?", calendar.getTime(), false);
		if(deleted > 0){
			Logger.info("%d non-activated subscription was deleted.", deleted);
		}
	}

}
