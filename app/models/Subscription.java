package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: eguller
 * Date: 3/27/14
 * Time: 8:18 AM
 */
@Entity
@Table(name = "SUBSCRIPTION")
public class Subscription extends Model {
    public static final Subscription NONE = new Subscription();
    @Column(name = "EMAIL", nullable = false, unique = true)
    String email = "";
    @Column(name = "DAILY")
    boolean daily = false;
    @Column(name = "WEEKLY")
    boolean weekly = true;
    @Column(name = "MONTHLY")
    boolean monthly = false;
    @Column(name = "ANNUALLY")
    boolean annually = false;
    @Column(name = "SUBSUUID", nullable = false)
    String subsUUID;
    @Column(name = "SUBSCRIPTION_DATE", nullable = false)
    Date subscriptionDate;
    @Column(name = "ACTIVATION_DATE", nullable = true)
    Date activationDate;
    @Column(name = "ACTIVATED")
    boolean activated = false;
    @Column(name = "SENTDAY", nullable = false)
    int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    @Column(name = "SENTWEEK", nullable = false)
    int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    @Column(name = "SENTMONTH", nullable = false)
    int month = Calendar.getInstance().get(Calendar.MONTH);
    @Column(name = "SENTYEAR", nullable = false)
    int year = Calendar.getInstance().get(Calendar.YEAR);

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDaily() {
        return daily;
    }

    public void setDaily(boolean daily) {
        this.daily = daily;
    }

    public boolean isWeekly() {
        return weekly;
    }

    public void setWeekly(boolean weekly) {
        this.weekly = weekly;
    }

    public boolean isMonthly() {
        return monthly;
    }

    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
    }

    public boolean isAnnually() {
        return annually;
    }

    public void setAnnually(boolean annually) {
        this.annually = annually;
    }

    public String getSubsUUID() {
        return subsUUID;
    }

    public void setSubsUUID(String subsUUID) {
        this.subsUUID = subsUUID;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean subscribedBefore() {
        return Subscription.find("byEmail", email).fetch().size() > 0;
    }

    public static boolean deleteSubscription(String subscriptionId) {
        return Subscription.delete("subsUUID = ?", subscriptionId) > 0;
    }

    public static Subscription findBySubscriptionId(String subscriptionId) {

        List<Subscription> subscriptionList = Subscription.find("bySubsUUID", subscriptionId).fetch(1);
        if (subscriptionList.size() > 0) {
            return subscriptionList.get(0);
        } else {
            return Subscription.NONE;
        }
    }

    public void fixEmailFormat() {
        email = email.replaceAll("[ \t\n\r]", "").toLowerCase();
    }

    public boolean emailDomainExists() {
        return email.split("@").length == 2;
    }

    public String emailDomain() {
        String[] userAtDomain = email.split("@");
        if (userAtDomain.length == 2) {
            return userAtDomain[1];
        } else {
            return email;
        }
    }

    public void update(Subscription subscription) {
        this.setDaily(subscription.isDaily());
        this.setWeekly(subscription.isWeekly());
        this.setMonthly(subscription.isMonthly());
        this.setAnnually(subscription.isAnnually());
        this.save();
    }

    public static List<Subscription> dailySubscribers(int today){
        return Subscription.find("daily = ? and activated = ? and day != ? ", true, true, today).fetch();
    }

    public static List<Subscription> weeklySubscribers(int thisWeek){
        return Subscription.find("weekly = ? and activated = ? and week != ?", true, true, thisWeek).fetch();
    }

    public static List<Subscription> monthlySubscribers(int thisMonth){
        return Subscription.find("monthly = ? and activated = ? and month != ?", true, true, thisMonth).fetch();
    }

    public static List<Subscription> annualSubscribers(int thisYear){
        return Subscription.find("annually = ? and activated = ? and year != ?", true, true, thisYear).fetch();
    }
}
