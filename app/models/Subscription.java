package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * User: eguller
 * Date: 3/27/14
 * Time: 8:18 AM
 */
@Entity
@Table(name="SUBSCRIPTION")
public class Subscription extends Model {
    @Column(name="EMAIL", nullable = false, unique = true)
    String email = "";
    @Column(name="DAILY")
    boolean daily = false;
    @Column(name="WEEKLY")
    boolean weekly = true;
    @Column(name="MONTHLY")
    boolean monthly = false;
    @Column(name="ANNUALLY")
    boolean annually = false;
    @Column(name="SUBSUUID", nullable = false)
    String subsUUID;
    @Column(name="SUBSCRIPTION_DATE", nullable = false)
    Date subscriptionDate;
    @Column(name="ACTIVATION_DATE", nullable = true)
    Date activationDate;
    @Column(name="ACTIVATED")
    boolean activated = false;
    @Column(name="SENTDAY", nullable = false)
    int day = -1;
    @Column(name="SENTWEEK", nullable = false)
    int week = -1;
    @Column(name="SENTMONTH", nullable = false)
    int month = -1;
    @Column(name="SENTYEAR", nullable = false)
    int year = -1;

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

    public boolean subscribedBefore(){
        return Subscription.find("byEmail", email).fetch().size() > 0;
    }

    public static boolean deleteSubscription(String subscriptionId){
        return Subscription.delete("bySubsUUID", subscriptionId) > 0;
    }

    public void fixEmailFormat(){
        email = email.replaceAll("[ \t\n\r]", "").toLowerCase();
    }

    public boolean emailDomainExists(){
        return email.split("@").length == 2;
    }

    public String emailDomain(){
        String[] userAtDomain = email.split("@");
        if(userAtDomain.length == 2){
            return userAtDomain[1];
        } else {
            return email;
        }
    }
}
