package com.eguller.hntoplinks.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

@Table("subscription")
public class SubscriptionEntity implements HnEntity {
  @Id
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
  String        timeZone;
  @Column("next_send_day")
  LocalDateTime nextSendDay;
  @Column("next_send_week")
  LocalDateTime nextSendWeek;
  @Column("next_send_month")
  LocalDateTime nextSendMonth;
  @Column("next_send_year")
  LocalDateTime nextSendYear;

  private static Function<LocalDateTime, Boolean> isSubscriptionExpired = localDateTime -> {
    var expired = (localDateTime == null || LocalDateTime.now().isAfter(localDateTime));
    return expired;
  };


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

  public LocalDateTime
  getNextSendDay() {
    return nextSendDay;
  }

  public void setNextSendDay(LocalDateTime nextSendDay) {
    this.nextSendDay = nextSendDay;
  }

  public LocalDateTime getNextSendWeek() {
    return nextSendWeek;
  }

  public void setNextSendWeek(LocalDateTime nextSendWeek) {
    this.nextSendWeek = nextSendWeek;
  }

  public LocalDateTime getNextSendMonth() {
    return nextSendMonth;
  }

  public void setNextSendMonth(LocalDateTime nextSendMonth) {
    this.nextSendMonth = nextSendMonth;
  }

  public LocalDateTime getNextSendYear() {
    return nextSendYear;
  }

  public void setNextSendYear(LocalDateTime nextSendYear) {
    this.nextSendYear = nextSendYear;
  }

  public boolean isDailyExpired() {
    var expired = isSubscriptionExpired.apply(this.nextSendDay);
    return expired;
  }

  public boolean isWeeklyExpired(){
    var expired = isSubscriptionExpired.apply(this.nextSendWeek);
    return expired;
  }

  public boolean isMonthlyExpired(){
    var expired = isSubscriptionExpired.apply(this.nextSendMonth);
    return expired;
  }

  public boolean isAnnuallyExpired(){
    var expired = isSubscriptionExpired.apply(this.nextSendYear);
    return expired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriptionEntity that = (SubscriptionEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
