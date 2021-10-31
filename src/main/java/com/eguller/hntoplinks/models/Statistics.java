package com.eguller.hntoplinks.models;

import java.time.LocalDateTime;

public class Statistics {
    public Statistics(long subscriberCount, long activeSubscriberCount, long dailySubscriberCount, long weeklySubscriberCount, long monthlySubscriberCount, long annuallySubscriberCount, long unsubscribeCount, long successEmailCount, long failureEmailCount, LocalDateTime lastEmailSent, LocalDateTime lastHnUpdate) {
        this.subscriberCount = subscriberCount;
        this.activeSubscriberCount = activeSubscriberCount;
        this.dailySubscriberCount = dailySubscriberCount;
        this.weeklySubscriberCount = weeklySubscriberCount;
        this.monthlySubscriberCount = monthlySubscriberCount;
        this.annuallySubscriberCount = annuallySubscriberCount;
        this.unsubscribeCount = unsubscribeCount;
        this.successEmailCount = successEmailCount;
        this.failureEmailCount = failureEmailCount;
        this.lastEmailSent = lastEmailSent;
        this.lastHnUpdate = lastHnUpdate;
    }

    private long subscriberCount;
    private long activeSubscriberCount;
    private long dailySubscriberCount;
    private long weeklySubscriberCount;
    private long monthlySubscriberCount;
    private long annuallySubscriberCount;
    private long unsubscribeCount;
    private long successEmailCount;
    private long failureEmailCount;
    private LocalDateTime lastEmailSent;
    private LocalDateTime lastHnUpdate;



    public static class Builder {
        private long subscriberCount;
        private long activeSubscriberCount;
        private long dailySubscriberCount;
        private long weeklySubscriberCount;
        private long monthlySubscriberCount;
        private long annuallySubscriberCount;
        private long unsubscribeCount;
        private long successEmailCount;
        private long failureEmailCount;
        private LocalDateTime lastEmailSent;
        private LocalDateTime lastHnUpdate;

        public Builder setSubscriberCount(long subscriberCount) {
            this.subscriberCount = subscriberCount;
            return this;
        }

        public Builder setActiveSubscriberCount(long activeSubscriberCount) {
            this.activeSubscriberCount = activeSubscriberCount;
            return this;
        }

        public Builder setDailySubscriberCount(long dailySubscriberCount) {
            this.dailySubscriberCount = dailySubscriberCount;
            return this;
        }

        public Builder setWeeklySubscriberCount(long weeklySubscriberCount) {
            this.weeklySubscriberCount = weeklySubscriberCount;
            return this;
        }

        public Builder setMonthlySubscriberCount(long monthlySubscriberCount) {
            this.monthlySubscriberCount = monthlySubscriberCount;
            return this;
        }

        public Builder setAnnuallySubscriberCount(long annuallySubscriberCount) {
            this.annuallySubscriberCount = annuallySubscriberCount;
            return this;
        }

        public Builder setUnsubscribeCount(long unsubscribeCount) {
            this.unsubscribeCount = unsubscribeCount;
            return this;
        }

        public Builder setSuccessEmailCount(long successEmailCount) {
            this.successEmailCount = successEmailCount;
            return this;
        }

        public Builder setFailureEmailCount(long failureEmailCount) {
            this.failureEmailCount = failureEmailCount;
            return this;
        }

        public Builder setLastEmailSent(LocalDateTime lastEmailSent) {
            this.lastEmailSent = lastEmailSent;
            return this;
        }

        public Builder setLastHnUpdate(LocalDateTime lastHnUpdate) {
            this.lastHnUpdate = lastHnUpdate;
            return this;
        }

        public Statistics createStatistics() {
            return new Statistics(subscriberCount, activeSubscriberCount, dailySubscriberCount, weeklySubscriberCount, monthlySubscriberCount, annuallySubscriberCount, unsubscribeCount, successEmailCount, failureEmailCount, lastEmailSent, lastHnUpdate);
        }
    }
}
