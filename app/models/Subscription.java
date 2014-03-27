package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * User: eguller
 * Date: 3/27/14
 * Time: 8:18 AM
 */
@Entity
public class Subscription extends Model {
    @Column(name="EMAIL")
    String email;
    @Column(name="DAILY")
    boolean daily;
    @Column(name="WEEKLY")
    boolean weekly;
    @Column(name="MONTHLY")
    boolean monthly;
    @Column(name="ANNUALLY")
    boolean annually;
    @Column(name="SUBSUUID")
    String subsUUID;
    @Column(name="SUBSCRIPTION_DATE")
    Date subscriptionDate;
    @Column(name="VALIDATION_DATE")
    Date validationDate;
    @Column(name="VALIDATED")
    boolean validated;

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

    public Date getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(Date validationDate) {
        this.validationDate = validationDate;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }


}
