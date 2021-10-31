package com.eguller.hntoplinks.models;

public enum StatKey {
    SUBSCRIBERS("Subscriber"),
    ACTIVE_SUBSCRIBER("Active Subscriber"),
    DAILY_SUBSCRIBER("Daily Subscriber"),
    WEEKLY_SUBSCRIBER("Weekly Subscriber"),
    MONTHLY_SUBSCRIBER("Monthly Subscriber"),
    ANNUALLY_SUBSCRIBER("Annually Subscriber"),
    UNSUBSCRIBES("Un-Subscribes"),
    SUCCESS_EMAIL_COUNT("Success Email"),
    FAILURE_EMAIL_COUNT("Failure Email"),
    LAST_EMAIL_SENT("Last Email Sent"),
    LAST_HN_UPDATE("Last HN Update");
    String displayName;

    StatKey(String displayName) {
        this.displayName = displayName;
    }



    @Override
    public String toString() {
        return displayName;
    }


}