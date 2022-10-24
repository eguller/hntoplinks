package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.services.EmailService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.TemplateService;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public abstract class TopLinksEmail {
  private static final int               MAX_STORY_COUNT = 25;
  protected            SubscriberEntity  subscription;
  protected            StoryCacheService storyCacheService;
  protected            TemplateService   templateService;

  public Email createEmail() {
    var subject = createSubject();
    var topStories = getTopStories();
    var content = templateService.generateTopEmail(subject, subscription, topStories.subList(0, Math.min(topStories.size() - 1, MAX_STORY_COUNT)));

    var email = Email.builder()
      .subject("[hntoplinks] - " + subject)
      .to(subscription.getEmail())
      .html(content).build();

    return email;
  }

  protected abstract String createSubject();

  protected abstract List<StoryEntity> getTopStories();
}
