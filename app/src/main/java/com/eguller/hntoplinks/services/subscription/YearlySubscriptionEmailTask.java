package com.eguller.hntoplinks.services.subscription;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.TemplateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class YearlySubscriptionEmailTask extends SubscriptionEmailTask {
  private final StoryRepository storyRepository;

  public YearlySubscriptionEmailTask(TemplateService templateService, EmailTarget emailTarget, EmailProviderService emailProviderService, StoryRepository storyRepository) {
    super(templateService, emailTarget, emailProviderService);
    this.storyRepository = storyRepository;
  }

  @Override
  protected String getSubject() {
    String lastYear = DateTimeFormatter.ofPattern("YYYY").format(LocalDateTime.now().minusYears(1).atZone(emailTarget.subscriber().getTimeZoneObj()));
    return "Best of " + lastYear;
  }

  @Override
  protected List<StoryEntity> getStories() {
    var stories = this.storyRepository.readyAnnuallyTop();
    return stories;
  }
}
