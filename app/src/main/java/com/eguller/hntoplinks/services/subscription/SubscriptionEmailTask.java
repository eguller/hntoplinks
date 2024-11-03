package com.eguller.hntoplinks.services.subscription;

import java.time.LocalDateTime;
import java.util.List;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.models.Email;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.TemplateService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class SubscriptionEmailTask {
  private final int MAX_STORY_COUNT = 25;

  private final TemplateService templateService;

  protected final EmailTarget emailTarget;

  private EmailProviderService emailProviderService;

  public void execute() {
    var subject = getSubject();
    var stories = getStories();
    var maxStoryCount = getMaxStoryCount();
    var topStories = stories.subList(0, Math.min(stories.size() - 1, maxStoryCount));
    var content = templateService.generateTopEmail(subject, emailTarget.subscriber(), topStories);
    var email =
        Email.builder()
            .subject("[hntoplinks] - " + subject)
            .to(emailTarget.subscriber().getEmail())
            .html(content)
            .build();
    emailProviderService.send(email);
    emailTarget.subscription().setNextSendDate(getNextSendDate());
  }

  protected abstract LocalDateTime getNextSendDate();

  protected abstract String getSubject();

  protected abstract List<StoryEntity> getStories();

  protected int getMaxStoryCount() {
    return MAX_STORY_COUNT;
  }
}
