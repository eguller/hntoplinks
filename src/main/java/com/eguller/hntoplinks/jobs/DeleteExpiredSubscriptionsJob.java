package com.eguller.hntoplinks.jobs;

import com.eguller.hntoplinks.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeleteExpiredSubscriptionsJob {
	@Autowired
	private SubscriptionService subscriptionService;

	@Scheduled(cron = "0 0 1 ? * *")
	public void doJob(){
		subscriptionService.deleteExpiredInactiveSubscriptions();
	}

}