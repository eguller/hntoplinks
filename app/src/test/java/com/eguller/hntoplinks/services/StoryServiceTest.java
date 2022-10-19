package com.eguller.hntoplinks.services;


import com.eguller.hntoplinks.Application;
import com.eguller.hntoplinks.controllers.ApplicationController;
import com.eguller.hntoplinks.jobs.SendMailJob;
import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.models.Story;
import com.eguller.hntoplinks.models.Subscription;
import com.eguller.hntoplinks.models.SubscriptionForm;
import com.eguller.hntoplinks.models.SubscriptionPage;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import com.eguller.hntoplinks.util.SubscriptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Supplier;

@SpringBootTest(classes = {Application.class})
@ActiveProfiles({"local"})
public class StoryServiceTest {
  @Autowired
  private StoryService storyService;

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
    var hnStory = new Story(null, 1L, 59, "Test", "https://www.hntoplinks.com", "hntoplinks.com", "eguller", 99, LocalDateTime.now());
    storyService.saveStories(List.of(hnStory));
    storyCacheService.addNewStories(List.of(hnStory));


    var savedEntity = storyRepository.findByHnid(1L);
    Assertions.assertNotNull(savedEntity);

    var hnStoryUpdated = new Story(null, 1L, 59, "Test", "https://www.hntoplinks.com", "hntoplinks.com", "eguller", 100, LocalDateTime.now());
    storyService.saveStories(List.of(hnStoryUpdated));
    storyCacheService.addNewStories(List.of(hnStoryUpdated));
    var updatedEntity = storyRepository.findByHnid(1L);

    Assertions.assertEquals(savedEntity.get().getId(), updatedEntity.get().getId());


    long count = storyCacheService.getAllTimeTop().stream().filter(story -> story.hnId() == 1).count();
    Assertions.assertEquals(1, count); //there should not be any duplicate.
  }

  @Test
  public void test_sendDailyEmail() {
    var storyTitle = "Daily Mail Test - " + UUID.randomUUID();
    var hnStory = new Story(null, (new Random().nextLong(Long.MAX_VALUE)), (new Random().nextInt(Integer.MAX_VALUE)), storyTitle, "https://daily.mail.test.hntoplinks.com", "hntoplinks.com", "daily_mail_test_user", (new Random().nextInt(Integer.MAX_VALUE)), LocalDateTime.now().minusHours(1));
    var emailAddress = "test_daily_mail1@hntoplinks.com";
    var subscription = Subscription.builder()
      .email(emailAddress)
      .daily(true)
      .timeZone(ZoneId.of("UTC"))
      .build();
    test_PeriodicEmail(hnStory, subscription);
    test_PeriodicEmail(hnStory, subscription);

    var email = mockEmailStore.getLastMail(emailAddress);
    Assertions.assertTrue(email.isPresent());
    Assertions.assertTrue(email.get().getHtml().contains(hnStory.title()));
  }

  @Test
  public void test_sendWeeklyEmail() {
    var storyTitle = "Weekly Mail Test - " + UUID.randomUUID();
    var hnStory = new Story(null, (new Random().nextLong(Long.MAX_VALUE)), (new Random().nextInt(Integer.MAX_VALUE)), storyTitle, "https://daily.mail.test.hntoplinks.com", "hntoplinks.com", "daily_mail_test_user", (new Random().nextInt(Integer.MAX_VALUE)), LocalDateTime.now().minusDays(3));
    var emailAddress = "test_weekly_mail1@hntoplinks.com";
    var subscription = Subscription.builder()
      .email(emailAddress)
      .weekly(true)
      .timeZone(ZoneId.of("UTC"))
      .build();
    test_PeriodicEmail(hnStory, subscription);
  }

  @Test
  public void test_sendMonthlyEmail() {
    var storyTitle = "Monthly Mail Test - " + UUID.randomUUID();
    var hnStory = new Story(null, (new Random().nextLong(Long.MAX_VALUE)), (new Random().nextInt(Integer.MAX_VALUE)), storyTitle, "https://daily.mail.test.hntoplinks.com", "hntoplinks.com", "daily_mail_test_user", (new Random().nextInt(Integer.MAX_VALUE)), LocalDateTime.now().minusDays(10));
    var emailAddress = "test_monthly_mail1@hntoplinks.com";
    var subscription = Subscription.builder()
      .email(emailAddress)
      .monthly(true)
      .timeZone(ZoneId.of("UTC"))
      .build();
    test_PeriodicEmail(hnStory, subscription);
  }

  @Test
  public void test_sendAnnualEmail() {
    var storyTitle = "Annual Mail Test - " + UUID.randomUUID();
    var hnStory = new Story(null, (new Random().nextLong(Long.MAX_VALUE)), (new Random().nextInt(Integer.MAX_VALUE)), storyTitle, "https://daily.mail.test.hntoplinks.com", "hntoplinks.com", "daily_mail_test_user", (new Random().nextInt(Integer.MAX_VALUE)), LocalDateTime.now().minusDays(90));
    var emailAddress = "test_annual_mail1@hntoplinks.com";
    var subscription = Subscription.builder()
      .email(emailAddress)
      .annually(true)
      .timeZone(ZoneId.of("UTC"))
      .build();
    test_PeriodicEmail(hnStory, subscription);
  }

  private void test_PeriodicEmail(Story story, Subscription subscription) {
    test_PeriodicEmail(List.of(story), subscription);
  }

  private void test_PeriodicEmail(List<Story> stories, Subscription subscription) {
    storyService.saveStories(stories);
    storyCacheService.addNewStories(stories);
    var subscriptionForm = SubscriptionForm.builder().subscription(subscription).build();
    var model = new ExtendedModelMap();
    applicationController.subscribe_Post(subscriptionForm, null, model);

    var subscriberId = ((SubscriptionPage) model.get("page")).getSubscription().getSubsUUID();

    var parameters = new HashMap<String, Object>();
    parameters.put("nextSendDate", LocalDateTime.now().minusHours(1)); //next_send time already passed should trigger an email send.
    parameters.put("subsuuid", subscriberId);
    namedParameterJdbcTemplate.update("update subscription set next_send_day = :nextSendDate where subsuuid = :subsuuid", parameters);
    namedParameterJdbcTemplate.update("update subscription set next_send_week = :nextSendDate where subsuuid = :subsuuid", parameters);
    namedParameterJdbcTemplate.update("update subscription set next_send_month = :nextSendDate where subsuuid = :subsuuid", parameters);
    namedParameterJdbcTemplate.update("update subscription set next_send_year = :nextSendDate where subsuuid = :subsuuid", parameters);

    sendMailJob.sendEmail();

    var email = mockEmailStore.getLastMail(subscription.getEmail());
    Assertions.assertTrue(email.isPresent());
    stories.forEach(story -> Assertions.assertTrue(email.get().getHtml().contains(story.title())));
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
