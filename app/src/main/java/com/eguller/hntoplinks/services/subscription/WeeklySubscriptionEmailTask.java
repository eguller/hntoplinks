package com.eguller.hntoplinks.services.subscription;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.TemplateService;
import com.eguller.hntoplinks.util.DateUtils;

public class WeeklySubscriptionEmailTask extends SubscriptionEmailTask {
  private final StoryRepository storyRepository;

  public WeeklySubscriptionEmailTask(
      TemplateService templateService,
      EmailTarget emailTarget,
      EmailProviderService emailProviderService,
      StoryRepository storyRepository) {
    super(templateService, emailTarget, emailProviderService);
    this.storyRepository = storyRepository;
  }

  @Override
  protected LocalDateTime getNextSendDate() {
    var nextSendDate = DateUtils.nextMonday_7_AM(emailTarget.subscriber().getTimeZoneObj());
    return nextSendDate;
  }

  @Override
  protected String getSubject() {
    var fromDate =
        LocalDateTime.now().minusDays(7).atZone(emailTarget.subscriber().getTimeZoneObj());
    var toDate = LocalDateTime.now().minusDays(1).atZone(emailTarget.subscriber().getTimeZoneObj());

    var fromDateStr = DateTimeFormatter.ofPattern("dd MMMM").format(fromDate);
    var toDateStr = DateTimeFormatter.ofPattern("dd MMMM").format(toDate);

    // if week is in same month, display 14 - 21 June instead of 14 June - 21 June
    if (fromDate.getMonth().equals(toDate.getMonth())) {
      fromDateStr = DateTimeFormatter.ofPattern("dd").format(fromDate);
    }

    return String.format("%s - %s Weekly Top Links", fromDateStr, toDateStr);
  }

  @Override
  protected List<StoryEntity> getStories() {
    var stories = this.storyRepository.readWeeklyTop();
    return stories;
  }
}
