package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;

public record EmailTarget(SubscriberEntity subscriber, SubscriptionEntity subscription) {
}
