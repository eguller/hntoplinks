package com.eguller.hntoplinks.services;


import com.eguller.hntoplinks.Application;
import com.eguller.hntoplinks.controllers.ApplicationController;
import com.eguller.hntoplinks.models.Story;
import com.eguller.hntoplinks.models.Subscription;
import com.eguller.hntoplinks.models.SubscriptionForm;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

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
  private ApplicationController appicationController;

  @Autowired
  private SubscriptionRepository subscriptionRepository;


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
  public void test_subscribe(){
    var emailAddress = "abc@def.com";
    var subscription = Subscription.builder()
      .email(emailAddress)
      .daily(true)
      .timeZone("UTC")
      .build();
    var subscriptionForm = SubscriptionForm.builder().subscription(subscription).build();

    var model = new ExtendedModelMap();
    appicationController.subscribe_Post(subscriptionForm, null, model, TimeZone.getTimeZone("UTC"));

    var subscriptionEntity = subscriptionRepository.findByEmail(emailAddress);
    Assertions.assertEquals(emailAddress, subscriptionEntity.get().getEmail());
    Assertions.assertTrue(subscriptionEntity.get().isDaily());
    Assertions.assertFalse(subscriptionEntity.get().isWeekly());
    Assertions.assertFalse(subscriptionEntity.get().isMonthly());
    Assertions.assertFalse(subscriptionEntity.get().isAnnually());

  }
}
