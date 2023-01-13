package com.eguller.hntoplinks.services.subscription;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.TemplateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeeklySubscriptionEmailTask extends SubscriptionEmailTask {
  private final StoryRepository storyRepository;
  public WeeklySubscriptionEmailTask(TemplateService templateService, EmailTarget emailTarget, EmailProviderService emailProviderService, StoryRepository storyRepository) {
    super(templateService, emailTarget, emailProviderService);
    this.storyRepository = storyRepository;
  }

  @Override
    protected String getSubject() {
      String toDate = DateTimeFormatter.ofPattern("dd MMMM").format(LocalDateTime.now().minusDays(1).atZone(emailTarget.subscriber().getTimeZoneObj()));
      String fromDate = DateTimeFormatter.ofPattern("dd MMMM").format(LocalDateTime.now().minusDays(7).atZone(emailTarget.subscriber().getTimeZoneObj()));
      return String.format("%s - %s Weekly Top Links", fromDate, toDate);
    }

    @Override
    protected List<StoryEntity> getStories() {
      var stories = this.storyRepository.readWeeklyTop();
      return stories;
    }
  }
