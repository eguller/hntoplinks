package com.eguller.hntoplinks.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.ui.ExtendedModelMap;
import org.testcontainers.containers.PostgreSQLContainer;

import com.eguller.hntoplinks.Application;
import com.eguller.hntoplinks.controllers.StoriesController;
import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.jobs.SendMailJob;
import com.eguller.hntoplinks.models.SubscriptionForm;
import com.eguller.hntoplinks.models.SubscriptionPage;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.repository.SubscribersRepository;
import com.eguller.hntoplinks.services.email.MockEmailStore;
import com.eguller.hntoplinks.util.DbUtil;
import com.eguller.hntoplinks.util.SubscriptionUtil;

@SpringBootTest(classes = {Application.class})
@ActiveProfiles({"local", "test"})
public class StoryServiceTest {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.5");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    DbUtil.updateDatabaseProperties(postgres, registry);
  }

  @Autowired private SubscribersRepository subscriberRepository;
  @Autowired private StoryRepository storyRepository;

  @Autowired private StoriesController applicationController;

  @Autowired private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired private SendMailJob sendMailJob;

  @Autowired private MockEmailStore mockEmailStore;

  @Test
  public void test_saveStory() {
    var randomHnId = generateRandomHnId();
    var hnStory = new StoryEntity();
    hnStory.setHnid(randomHnId);
    hnStory.setComhead("hntoplinks.com");
    hnStory.setUser("eguller");
    hnStory.setUrl("https://www.hntoplinks.com");
    hnStory.setTitle("Title");
    hnStory.setPoints(59);
    hnStory.setComment(99);
    hnStory.setDate(LocalDateTime.now());
    hnStory.setLastUpdate(LocalDateTime.now());

    storyRepository.saveStories(List.of(hnStory));

    var savedEntity = storyRepository.findByHnid(randomHnId);
    Assertions.assertNotNull(savedEntity);

    var hnStoryUpdated = new StoryEntity();
    hnStoryUpdated.setHnid(randomHnId);
    hnStoryUpdated.setComhead("hntoplinks.com");
    hnStoryUpdated.setUser("eguller");
    hnStoryUpdated.setUrl("https://www.hntoplinks.com");
    hnStoryUpdated.setTitle("Title");
    hnStoryUpdated.setPoints(66);
    hnStoryUpdated.setComment(108);
    hnStoryUpdated.setDate(LocalDateTime.now());
    hnStoryUpdated.setLastUpdate(LocalDateTime.now());

    storyRepository.saveStories(List.of(hnStoryUpdated));

    var updatedEntity = storyRepository.findByHnid(randomHnId);

    Assertions.assertEquals(savedEntity.get().getId(), updatedEntity.get().getId());
  }

  @Test
  public void test_sendDailyEmail() {
    var storyTitle = "Daily Mail Test - " + UUID.randomUUID();
    var hnStory = new StoryEntity();
    hnStory.setHnid(generateRandomHnId());
    hnStory.setPoints((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setComment((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setTitle(storyTitle);
    hnStory.setUrl("https://daily.mail.test.hntoplinks.com");
    hnStory.setDate(LocalDateTime.now().minusHours(1));
    hnStory.setLastUpdate(LocalDateTime.now().minusHours(1));
    hnStory.setUser("daily_mail_test_user");

    var emailAddress = "test_daily_mail1@hntoplinks.com";
    DbUtil.deleteSubscriber(namedParameterJdbcTemplate, emailAddress);
    var subscriber = new SubscriberEntity();
    subscriber.setEmail(emailAddress);
    subscriber.setTimeZone("UTC");
    var subscription = new SubscriptionEntity();
    subscription.setPeriod(Period.DAILY);
    subscription.setNextSendDate(LocalDateTime.now().minusDays(1));
    subscriber.getSubscriptionList().add(subscription);

    test_PeriodicEmail(hnStory, subscriber);
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
    DbUtil.deleteSubscriber(namedParameterJdbcTemplate, emailAddress);

    var subscriber = new SubscriberEntity();
    subscriber.setEmail(emailAddress);
    subscriber.setTimeZone("UTC");

    var weeklySubscription = new SubscriptionEntity();
    weeklySubscription.setPeriod(Period.WEEKLY);
    weeklySubscription.setNextSendDate(LocalDateTime.now().minusDays(1));
    subscriber.getSubscriptionList().add(weeklySubscription);

    test_PeriodicEmail(hnStory, subscriber);
  }

  @Test
  public void send_daily_and_weekly_email() {
    var storyTitle = "Daily and Weekly Mail Test - " + UUID.randomUUID();

    var hnStory = new StoryEntity();
    hnStory.setHnid((new Random().nextLong(Long.MAX_VALUE)));
    hnStory.setPoints((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setComment((new Random().nextInt(Integer.MAX_VALUE)));
    hnStory.setTitle(storyTitle);
    hnStory.setUrl("https://daily_and_weekly.mail.test.hntoplinks.com");
    hnStory.setDate(LocalDateTime.now().minusHours(5));
    hnStory.setLastUpdate(LocalDateTime.now().minusDays(5));
    hnStory.setUser("daily_and_weekly_mail_test_user");

    var emailAddress = "test_daily_and_weekly_mail1@hntoplinks.com";
    DbUtil.deleteSubscriber(namedParameterJdbcTemplate, emailAddress);

    var subscriber = new SubscriberEntity();
    subscriber.setEmail(emailAddress);
    subscriber.setTimeZone("UTC");

    var dailySubscription = new SubscriptionEntity();
    dailySubscription.setPeriod(Period.DAILY);
    dailySubscription.setNextSendDate(LocalDateTime.now().minusDays(1));
    subscriber.getSubscriptionList().add(dailySubscription);

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
    DbUtil.deleteSubscriber(namedParameterJdbcTemplate, emailAddress);
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
    DbUtil.deleteSubscriber(namedParameterJdbcTemplate, emailAddress);
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

    var subscriptionFormBuilder = SubscriptionForm.builder();
    subscriptionFormBuilder.email(subscriber.getEmail());
    subscriptionFormBuilder.selectedPeriods(
        subscriber.getSubscriptionList().stream()
            .map(subscriptionEntity -> subscriptionEntity.getPeriod())
            .collect(Collectors.toSet()));

    var subscriptionForm = subscriptionFormBuilder.build();
    var model = new ExtendedModelMap();
    applicationController.subscribe_Post(subscriptionForm, model);
    mockEmailStore.reset(subscriber.getEmail());

    var subscriberUUID = ((SubscriptionPage) model.get("page")).getSubscriptionForm().getSubsUUID();
    var savedSubscriber = subscriberRepository.findBySubscriberId(subscriberUUID);

    var parameters = new HashMap<String, Object>();
    parameters.put(
        "nextSendDate",
        LocalDateTime.now()
            .minusHours(1)); // next_send time already passed should trigger an email send.
    parameters.put("subscriberId", savedSubscriber.get().getId());
    namedParameterJdbcTemplate.update(
        "update subscription set next_send_date = :nextSendDate where period = 'DAILY' and subscriber_id = :subscriberId",
        parameters);
    namedParameterJdbcTemplate.update(
        "update subscription set next_send_date = :nextSendDate where period = 'WEEKLY' and subscriber_id = :subscriberId",
        parameters);
    namedParameterJdbcTemplate.update(
        "update subscription set next_send_date = :nextSendDate where period = 'MONTHLY' and subscriber_id = :subscriberId",
        parameters);
    namedParameterJdbcTemplate.update(
        "update subscription set next_send_date = :nextSendDate where period = 'YEARLY' and subscriber_id = :subscriberId",
        parameters);

    sendMailJob.sendEmail();

    var email = mockEmailStore.getLastMail(subscriber.getEmail());
    Assertions.assertTrue(email.isPresent());
    stories.forEach(
        story -> Assertions.assertTrue(email.get().getHtml().contains(story.getTitle())));
    var emailCount = mockEmailStore.emailCount(subscriber.getEmail());

    // 1 extra email is subscribe_Post (subscription email)
    Assertions.assertEquals(subscriber.getSubscriptionList().size(), emailCount);
    mockEmailStore.reset();

    sendMailJob.sendEmail();
    emailCount = mockEmailStore.emailCount(subscriber.getEmail());
    // subscription emails sent in previous step. It should not send again.
    Assertions.assertEquals(0, emailCount);
  }

  @Test
  public void test_receiveEmail() {
    var activeUserEmailAddress = "test_active_user1@hntoplinks.com";
    var inActiveUserEmailAddress = "test_inactive_user1@hntoplinks.com";
    SubscriptionUtil.subscribeDailyNew(this.applicationController, activeUserEmailAddress);
    SubscriptionUtil.subscribeDailyNew(this.applicationController, activeUserEmailAddress);

    var queryParams = new HashMap<String, String>();
    queryParams.put("email", inActiveUserEmailAddress);
    namedParameterJdbcTemplate.update(
        "update subscriber set activated = false where email=:email", queryParams);

    var parameters = new HashMap<String, Object>();
    parameters.put(
        "nextSendDate",
        LocalDateTime.now()
            .minusHours(1)); // next_send time alread passed should trigger an email send.
    namedParameterJdbcTemplate.update(
        "update subscription set next_send_date = :nextSendDate", parameters);

    sendMailJob.sendEmail();

    var activeUserEmail = mockEmailStore.getLastMail(activeUserEmailAddress);
    var inactiveUserEmail = mockEmailStore.getLastMail(inActiveUserEmailAddress);

    Assertions.assertTrue(activeUserEmail.isPresent());
    Assertions.assertFalse(inactiveUserEmail.isPresent());
  }

  private Long generateRandomHnId() {
    return new Random().nextLong(1000, Integer.MAX_VALUE);
  }
}
