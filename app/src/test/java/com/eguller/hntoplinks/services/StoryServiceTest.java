package com.eguller.hntoplinks.services;


import com.eguller.hntoplinks.Application;
import com.eguller.hntoplinks.controllers.ApplicationController;
import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.jobs.SendMailJob;
import com.eguller.hntoplinks.models.SubscriptionPage;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.repository.SubscriberRepository;
import com.eguller.hntoplinks.util.SubscriptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(classes = {Application.class})
@ActiveProfiles({"local"})
public class StoryServiceTest {

  @Autowired
  private SubscriberRepository subscriberRepository;
  @Autowired
  private StoryRepository storyRepository;

  @Autowired
  private StoryCacheService storyCacheService;

  @Autowired
  private ApplicationController applicationController;

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private SendMailJob sendMailJob;

  @Autowired
  private MockEmailStore mockEmailStore;

  @Test
  public void test_saveStory() {
    var hnStory = new StoryEntity();
    hnStory.setHnid(1L);
    hnStory.setComhead("hntoplinks.com");
    hnStory.setUser("eguller");
    hnStory.setUrl("https://www.hntoplinks.com");
    hnStory.setTitle("Title");
    hnStory.setPoints(59);
    hnStory.setComment(99);
    hnStory.setDate(LocalDateTime.now());
    hnStory.setLastUpdate(LocalDateTime.now());

    storyRepository.saveStories(List.of(hnStory));
    storyCacheService.addNewStories(List.of(hnStory));


    var savedEntity = storyRepository.findByHnid(1L);
    Assertions.assertNotNull(savedEntity);

    var hnStoryUpdated = new StoryEntity();
    hnStory.setHnid(1L);
    hnStory.setComhead("hntoplinks.com");
    hnStory.setUser("eguller");
    hnStory.setUrl("https://www.hntoplinks.com");
    hnStory.setTitle("Title");
    hnStory.setPoints(59);
    hnStory.setComment(10);
    hnStory.setDate(LocalDateTime.now());
    hnStory.setLastUpdate(LocalDateTime.now());

    storyRepository.saveStories(List.of(hnStoryUpdated));
    storyCacheService.addNewStories(List.of(hnStoryUpdated));

    var updatedEntity = storyRepository.findByHnid(1L);

    Assertions.assertEquals(savedEntity.get().getId(), updatedEntity.get().getId());


    long count = storyCacheService.getAllTimeTop().stream().filter(story -> story.getHnid() == 1).count();
    Assertions.assertEquals(1, count); //there should not be any duplicate.
  }

  @Test
  public void test_sendDailyEmail() {
    var storyTitle = "Daily Mail Test - " + UUID.randomUUID();
    var hnStory = new StoryEntity();
    hnStory.setHnid((new Random().nextLong(Long.MAX_VALUE)));
    hnStory.setPoints((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setComment((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setTitle(storyTitle);
    hnStory.setUrl("https://daily.mail.test.hntoplinks.com");
    hnStory.setDate(LocalDateTime.now().minusHours(1));
    hnStory.setLastUpdate(LocalDateTime.now().minusHours(1));
    hnStory.setUser("daily_mail_test_user");

    var emailAddress = "test_daily_mail1@hntoplinks.com";
    var subscriber = new SubscriberEntity();
    subscriber.setEmail(emailAddress);
    subscriber.setTimeZone("UTC");
    var subscription = new SubscriptionEntity();
    subscription.setPeriod(Period.DAILY);
    subscription.setNextSendDate(LocalDateTime.now().minusDays(1));


    test_PeriodicEmail(hnStory, subscriber);
    test_PeriodicEmail(hnStory, subscriber);

    var email = mockEmailStore.getLastMail(emailAddress);
    Assertions.assertTrue(email.isPresent());
    Assertions.assertTrue(email.get().getHtml().contains(hnStory.getTitle()));
  }

  @Test
  public void test_sendWeeklyEmail() {
    var storyTitle = "Weekly Mail Test - " + UUID.randomUUID();

    var hnStory = new StoryEntity();
    hnStory.setHnid((new Random().nextLong(Long.MAX_VALUE)));
    hnStory.setPoints((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setComment((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setTitle(storyTitle);
    hnStory.setUrl("https://weekly.mail.test.hntoplinks.com");
    hnStory.setDate(LocalDateTime.now().minusDays(3));
    hnStory.setLastUpdate(LocalDateTime.now().minusDays(3));
    hnStory.setUser("weekly_mail_test_user");

    var emailAddress = "test_weekly_mail1@hntoplinks.com";

    var subscriber = new SubscriberEntity();
    subscriber.setEmail(emailAddress);
    subscriber.setTimeZone("UTC");

    var subscription = new SubscriptionEntity();
    subscription.setPeriod(Period.WEEKLY);
    subscription.setNextSendDate(LocalDateTime.now().minusDays(1));
    subscriber.getSubscriptionList().add(subscription);

    test_PeriodicEmail(hnStory, subscriber);
  }

  @Test
  public void test_sendMonthlyEmail() {
    var storyTitle = "Monthly Mail Test - " + UUID.randomUUID();
    var hnStory = new StoryEntity();
    hnStory.setHnid((new Random().nextLong(Long.MAX_VALUE)));
    hnStory.setPoints((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setComment((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setTitle(storyTitle);
    hnStory.setUrl("https://monthly.mail.test.hntoplinks.com");
    hnStory.setDate(LocalDateTime.now().minusDays(10));
    hnStory.setLastUpdate(LocalDateTime.now().minusDays(10));
    hnStory.setUser("monthly_mail_test_user");

    var emailAddress = "test_monthly_mail1@hntoplinks.com";
    var subscriber = new SubscriberEntity();
    subscriber.setEmail(emailAddress);
    subscriber.setTimeZone("UTC");

    var subscription = new SubscriptionEntity();
    subscription.setPeriod(Period.MONTHLY);
    subscription.setNextSendDate(LocalDateTime.now().minusDays(1));
    subscriber.getSubscriptionList().add(subscription);

    test_PeriodicEmail(hnStory, subscriber);
  }

  @Test
  public void test_sendYearlyEmail() {
    var storyTitle = "Yearly Mail Test - " + UUID.randomUUID();
    var hnStory = new StoryEntity();
    hnStory.setHnid((new Random().nextLong(Long.MAX_VALUE)));
    hnStory.setPoints((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setComment((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setTitle(storyTitle);
    hnStory.setUrl("https://yearly.mail.test.hntoplinks.com");
    hnStory.setDate(LocalDateTime.now().minusDays(60));
    hnStory.setLastUpdate(LocalDateTime.now().minusDays(60));
    hnStory.setUser("yearly_mail_test_user");

    var emailAddress = "test_yearly_mail1@hntoplinks.com";
    var subscriber = new SubscriberEntity();
    subscriber.setEmail(emailAddress);
    subscriber.setTimeZone("UTC");

    var subscription = new SubscriptionEntity();
    subscription.setPeriod(Period.YEARLY);
    subscription.setNextSendDate(LocalDateTime.now().minusDays(1));
    subscriber.getSubscriptionList().add(subscription);

    test_PeriodicEmail(hnStory, subscriber);
  }

  private void test_PeriodicEmail(StoryEntity story, SubscriberEntity subscriber) {
    test_PeriodicEmail(List.of(story), subscriber);
  }

  private void test_PeriodicEmail(List<StoryEntity> stories, SubscriberEntity subscriber) {
    storyRepository.saveStories(stories);
    storyCacheService.addNewStories(stories);


    var subscriptionFormBuilder = SubscriptionPage.SubscriptionForm.builder();
    subscriptionFormBuilder.email(subscriber.getEmail());
    if(subscriber.isSubscribedFor(Period.DAILY)){
      subscriptionFormBuilder.daily(true);
    } else if(subscriber.isSubscribedFor(Period.WEEKLY)){
      subscriptionFormBuilder.weekly(true);
    } else if(subscriber.isSubscribedFor(Period.MONTHLY)){
      subscriptionFormBuilder.monthly(true);
    } else if(subscriber.isSubscribedFor(Period.YEARLY)){
      subscriptionFormBuilder.yearly(true);
    }

    var subscriptionForm = subscriptionFormBuilder.build();
    var model = new ExtendedModelMap();
    applicationController.subscribe_Post(subscriptionForm, model);

    var subscriberId = ((SubscriptionPage) model.get("page")).getSubscriptionForm().getSubsUUID();

    var parameters = new HashMap<String, Object>();
    parameters.put("nextSendDate", LocalDateTime.now().minusHours(1)); //next_send time already passed should trigger an email send.
    parameters.put("subsuuid", subscriberId);
    namedParameterJdbcTemplate.update("update subscription set next_send_day = :nextSendDate where subsuuid = :subsuuid", parameters);
    namedParameterJdbcTemplate.update("update subscription set next_send_week = :nextSendDate where subsuuid = :subsuuid", parameters);
    namedParameterJdbcTemplate.update("update subscription set next_send_month = :nextSendDate where subsuuid = :subsuuid", parameters);
    namedParameterJdbcTemplate.update("update subscription set next_send_year = :nextSendDate where subsuuid = :subsuuid", parameters);

    sendMailJob.sendEmail();

    var email = mockEmailStore.getLastMail(subscriber.getEmail());
    Assertions.assertTrue(email.isPresent());
    stories.forEach(story -> Assertions.assertTrue(email.get().getHtml().contains(story.getTitle())));
  }

  @Test
  public void test_receiveEmail() {
    var activeUserEmailAddress = "test_active_user1@hntoplinks.com";
    var inActiveUserEmailAddress = "test_inactive_user1@hntoplinks.com";
    SubscriptionUtil.subscribeDailyNew(this.applicationController, activeUserEmailAddress);
    SubscriptionUtil.subscribeDailyNew(this.applicationController, activeUserEmailAddress);

    var queryParams = new HashMap<String, String>();
    queryParams.put("email", inActiveUserEmailAddress);
    namedParameterJdbcTemplate.update("update subscription set activated = false where email=:email", queryParams);

    var parameters = new HashMap<String, Object>();
    parameters.put("nextSendDate", LocalDateTime.now().minusHours(1)); //next_send time alread passed should trigger an email send.
    namedParameterJdbcTemplate.update("update subscription set next_send_day = :nextSendDate", parameters);

    sendMailJob.sendEmail();

    var activeUserEmail = mockEmailStore.getLastMail(activeUserEmailAddress);
    var inactiveUserEmail = mockEmailStore.getLastMail(inActiveUserEmailAddress);

    Assertions.assertTrue(activeUserEmail.isPresent());
    Assertions.assertFalse(inactiveUserEmail.isPresent());
  }
}
