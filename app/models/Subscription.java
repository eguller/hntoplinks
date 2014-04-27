package models;

import play.Play;
import play.db.jpa.Model;
import utils.FutureCalculator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: eguller Date: 3/27/14 Time: 8:18 AM
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

	@Column(name = "TIMEZONE", nullable = false)
	String timeZone;
	@Column(name = "NEXT_SEND_DAY", nullable = false)
	Date nextSendDay;
	@Column(name = "NEXT_SEND_WEEK", nullable = false)
	Date nextSendWeek;
	@Column(name = "NEXT_SEND_MONTH", nullable = false)
	Date nextSendMonth;
	@Column(name = "NEXT_SEND_YEAR", nullable = false)
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

	public static boolean deleteSubscription(String subscriptionId) {
		return Subscription.delete("subsUUID = ?", subscriptionId) > 0;
	}

	public static Subscription findBySubscriptionId(String subscriptionId) {

		List<Subscription> subscriptionList = Subscription.find("bySubsUUID",
				subscriptionId).fetch(1);
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

	public static List<Subscription> dailySubscribers() {
		return Subscription.find("daily = ? and activated = ? and nextSendDay < ? ",
				true, true, Calendar.getInstance().getTime()).fetch();
	}

	public static List<Subscription> weeklySubscribers() {
		return Subscription.find("weekly = ? and activated = ? and nextSendWeek < ?",
				true, true, Calendar.getInstance().getTime()).fetch();
	}

	public static List<Subscription> monthlySubscribers() {
		return Subscription.find(
				"monthly = ? and activated = ? and nextSendMonth < ?", true, true,
				Calendar.getInstance().getTime()).fetch();
	}

	public static List<Subscription> annualSubscribers() {
		return Subscription.find(
				"annually = ? and activated = ? and nextSendYear < ?", true, true,
				Calendar.getInstance().getTime()).fetch();
	}

	public String getUnSubsribeUrl() {
		return new StringBuilder(
				Play.configuration.getProperty("application.baseUrl"))
				.append("unsubscribe/").append(this.getSubsUUID()).toString();
	}

	public String getModifyUrl() {
		return new StringBuilder(
				Play.configuration.getProperty("application.baseUrl"))
				.append("subscription/modify/").append(this.getSubsUUID())
				.toString();
	}

	public String getActivationUrl() {
		return new StringBuilder(
				Play.configuration.getProperty("application.baseUrl"))
				.append("subscription/activate/").append(this.getSubsUUID())
				.toString();
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public Date getNextSendDay() {
		return nextSendDay;
	}

	public Date getNextSendWeek() {
		return nextSendWeek;
	}

	public Date getNextSendMonth() {
		return nextSendMonth;
	}

	public Date getNextSendYear() {
		return nextSendYear;
	}
	
	public void setNextSendDay(Date nextSendDay) {
		this.nextSendDay = nextSendDay;
	}

	public void setNextSendWeek(Date nextSendWeek) {
		this.nextSendWeek = nextSendWeek;
	}

	public void setNextSendMonth(Date nextSendMonth) {
		this.nextSendMonth = nextSendMonth;
	}

	public void setNextSendYear(Date nextSendYear) {
		this.nextSendYear = nextSendYear;
	}

	public Calendar getSendDayInOwnTimeZone(){
		Calendar calendar = Calendar.getInstance(getTimZoneObj());
		calendar.setTime(nextSendDay);
		return calendar;
	}
	
	public Calendar getSendWeekInOwnTimeZone(){
		Calendar calendar = Calendar.getInstance(getTimZoneObj());
		calendar.setTime(nextSendWeek);
		return calendar;
	}
	
	public Calendar getSendMonthInOwnTimeZone(){
		Calendar calendar = Calendar.getInstance(getTimZoneObj());
		calendar.setTime(nextSendMonth);
		return calendar;
	}
	
	public Calendar getSendYearInOwnTimeZone(){
		Calendar calendar = Calendar.getInstance(getTimZoneObj());
		calendar.setTime(nextSendYear);
		return calendar;
	}
	
	public TimeZone getTimZoneObj(){
		return TimeZone.getTimeZone(timeZone);
	}


	public Date calculateNextSendDay() {
		return  new FutureCalculator(timeZone).getNextDay();
	}

	public Date calculateNextSendWeek() {
		return  new FutureCalculator(timeZone).getNextWeek();
	}

	public Date calculateNextSendMonth() {
		return new FutureCalculator(timeZone).getNextMonth();
	}

	public Date calculateNextSendYear() {
		return new FutureCalculator(timeZone).getNextYear();
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

}
