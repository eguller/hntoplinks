package com.eguller.hntoplinks.services.subscription;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.TemplateService;
import com.eguller.hntoplinks.util.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DailySubscriptionEmailTask extends SubscriptionEmailTask {
  private StoryRepository storyRepository;

  public DailySubscriptionEmailTask(TemplateService templateService, EmailTarget emailTarget, EmailProviderService emailProviderService, StoryRepository storyRepository) {
    super(templateService, emailTarget, emailProviderService);
    this.storyRepository = storyRepository;

  }

  @Override
  protected LocalDateTime getNextSendDate() {
    var nextSendDate = DateUtils.tomorrow_7_AM(emailTarget.subscriber().getTimeZoneObj());
    return nextSendDate;
  }

  @Override
  protected String getSubject() {
    String timePrefix = DateTimeFormatter.ofPattern("EEEE, dd MMMM").format(LocalDateTime.now().minusDays(1).atZone(emailTarget.subscriber().getTimeZoneObj()));
    return timePrefix + " - Daily Top Links";
  }

  @Override
  protected List<StoryEntity> getStories() {
    var stories = storyRepository.readDailyTop();
    return stories;
  }
}
