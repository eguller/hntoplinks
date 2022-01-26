package com.eguller.hntoplinks.entities;


import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("subscription")
public class SubscriptionEntity implements HnEntity {
  @Column("id")
  Long      id;
  @Column("email")
  String    email     = "";
  @Column("daily")
  boolean   daily     = false;
  @Column("weekly")
  boolean   weekly    = true;
  @Column("monthly")
  boolean   monthly   = false;
  @Column("annually")
  boolean   annually  = false;
  @Column("subsuuid")
  String    subsUUID;
  @Column("subscription_date")
  LocalDate subscriptionDate;
  @Column("activation_date")
  LocalDate activationDate;
  @Column("activated")
  boolean   activated = false;

  @Column("timezone")
  String    timeZone;
  @Column("next_send_day")
  LocalDate nextSendDay;
  @Column("next_send_week")
  LocalDate nextSendWeek;
  @Column("next_send_month")
  LocalDate nextSendMonth;
  @Column("next_send_year")
  LocalDate nextSendYear;

  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

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

  public LocalDate getSubscriptionDate() {
    return subscriptionDate;
  }

  public void setSubscriptionDate(LocalDate subscriptionDate) {
    this.subscriptionDate = subscriptionDate;
  }

  public LocalDate getActivationDate() {
    return activationDate;
  }

  public void setActivationDate(LocalDate activationDate) {
    this.activationDate = activationDate;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public LocalDate getNextSendDay() {
    return nextSendDay;
  }

  public void setNextSendDay(LocalDate nextSendDay) {
    this.nextSendDay = nextSendDay;
  }

  public LocalDate getNextSendWeek() {
    return nextSendWeek;
  }

  public void setNextSendWeek(LocalDate nextSendWeek) {
    this.nextSendWeek = nextSendWeek;
  }

  public LocalDate getNextSendMonth() {
    return nextSendMonth;
  }

  public void setNextSendMonth(LocalDate nextSendMonth) {
    this.nextSendMonth = nextSendMonth;
  }

  public LocalDate getNextSendYear() {
    return nextSendYear;
  }

  public void setNextSendYear(LocalDate nextSendYear) {
    this.nextSendYear = nextSendYear;
  }
}
