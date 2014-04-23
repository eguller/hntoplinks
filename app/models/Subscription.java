package models;

import play.Play;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: eguller
 * Date: 3/27/14
 * Time: 8:18 AM
 */
@Entity
@Table(name = "SUBSCRIPTION")
public class Subscription extends Model {
    @Transient
    private static final int SEND_HOUR = 7;
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

    @Column(name="TIMEZONE", nullable = false)
    String timeZone;
    @Column(name="NEXT_SEND_DATE")
    Date nextSendDay;
    @Column(name="NEXT_SEND_WEEK")
    Date nextSendWeek;
    @Column(name="NEXT_SEND_MONTH")
    Date nextSendMonth;
    @Column(name="NEXT_SEND_YEAR")
    Date nextSendYear;

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


    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static boolean deleteSubscription(String subscriptionId) {
        return Subscription.delete("subsUUID = ?", subscriptionId) > 0;
    }

    public static Subscription findBySubscriptionId(String subscriptionId) {

        List<Subscription> subscriptionList = Subscription.find("bySubsUUID", subscriptionId).fetch(1);
        if (subscriptionList.size() > 0) {
            return subscriptionList.get(0);
        } else {
            return null;
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

    public static List<Subscription> dailySubscribers(int today) {
        return Subscription.find("daily = ? and activated = ? and day != ? ", true, true, today).fetch();
    }

    public static List<Subscription> weeklySubscribers(int thisWeek) {
        return Subscription.find("weekly = ? and activated = ? and week != ?", true, true, thisWeek).fetch();
    }

    public static List<Subscription> monthlySubscribers(int thisMonth) {
        return Subscription.find("monthly = ? and activated = ? and month != ?", true, true, thisMonth).fetch();
    }

    public static List<Subscription> annualSubscribers(int thisYear) {
        return Subscription.find("annually = ? and activated = ? and year != ?", true, true, thisYear).fetch();
    }

    public String getUnSubsribeUrl() {
        return new StringBuilder(Play.configuration.getProperty("application.baseUrl")).append("unsubscribe/").append(this.getSubsUUID()).toString();
    }

    public String getModifyUrl() {
        return new StringBuilder(Play.configuration.getProperty("application.baseUrl")).append("subscription/modify/").append(this.getSubsUUID()).toString();
    }

    public String getActivationUrl() {
        return new StringBuilder(Play.configuration.getProperty("application.baseUrl")).append("subscription/activate/").append(this.getSubsUUID()).toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("subsUUID: ").append(subsUUID).append("\n");
        sb.append("daily: ").append(daily).append("\n");
        sb.append("weekly: ").append(weekly).append("\n");
        sb.append("monthly: ").append(monthly).append("\n");
        sb.append("annually").append(annually).append("\n");
        return sb.toString();
    }

    public void updateSendDates(){
        updateNextSendDay();
        updateNextSendWeek();
        updateNextSendMonth();
        updateNextSendYear();
    }

    public void updateNextSendDay(){
        TimeZone timeZone = TimeZone.getTimeZone(this.timeZone);
        Calendar calendar = Calendar.getInstance(timeZone);
        if(nextSendDay != null) {
            calendar.setTime(nextSendDay);

        }
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        setHour(calendar);
        nextSendDay = calendar.getTime();
    }

    public void updateNextSendWeek(){
        TimeZone timeZone = TimeZone.getTimeZone(this.timeZone);
        Calendar calendar = Calendar.getInstance(timeZone);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.clear();
        calendar.set(Calendar.HOUR, SEND_HOUR);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        nextSendWeek = calendar.getTime();
    }

    public void updateNextSendMonth(){

    }
    public void updateNextSendYear(){
        TimeZone timeZone = TimeZone.getTimeZone(this.timeZone);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.HOUR, SEND_HOUR);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.YEAR, year);
    }

    private static void setHour(Calendar calendar){
        calendar.set(Calendar.HOUR, SEND_HOUR);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
