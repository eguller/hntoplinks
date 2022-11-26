package com.eguller.hntoplinks.services.subscription;


import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.TemplateService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public abstract class SubscriptionEmailTask {
  private final int MAX_STORY_COUNT  = 25;

  private final TemplateService templateService;

  protected  final SubscriberEntity subscriber;

  protected final SubscriptionEntity subscription;

  private EmailProviderService emailProviderService;

  public void execute(){
    var subject = getSubject();
    var stories = getStories();
    var maxStoryCount = getMaxStoryCount();
    var topStories = stories.subList(0, Math.min(stories.size() - 1, getMaxStoryCount()));
    var content = templateService.generateTopEmail(subject, subscriber, topStories);
    var email = Email.builder()
      .subject("[hntoplinks] - " + subject)
      .to(subscriber.getEmail())
      .html(content).build();
    emailProviderService.send(email);
  }

  protected abstract String getSubject();

  protected abstract List<StoryEntity> getStories();

  protected int getMaxStoryCount(){
    return MAX_STORY_COUNT;
  }
}
