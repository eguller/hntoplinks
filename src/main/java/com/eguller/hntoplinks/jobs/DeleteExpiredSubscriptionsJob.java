package com.eguller.hntoplinks.jobs;

import com.eguller.hntoplinks.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


public class DeleteExpiredSubscriptionsJob {
	@Autowired
	private SubscriptionService subscriptionService;

	@Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
	public void doJob(){
		subscriptionService.deleteExpiredInactiveSubscriptions();
	}

}
