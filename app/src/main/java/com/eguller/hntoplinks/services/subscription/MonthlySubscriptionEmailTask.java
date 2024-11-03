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

public class MonthlySubscriptionEmailTask extends SubscriptionEmailTask {
  private final StoryRepository storyRepository;

  public MonthlySubscriptionEmailTask(
      TemplateService templateService,
      EmailTarget emailTarget,
      EmailProviderService emailProviderService,
      StoryRepository storyRepository) {
    super(templateService, emailTarget, emailProviderService);
    this.storyRepository = storyRepository;
  }

  @Override
  protected LocalDateTime getNextSendDate() {
    var nextSendDate =
        DateUtils.firstDayOfNextMonth_7_AM(emailTarget.subscriber().getTimeZoneObj());
    return nextSendDate;
  }

  @Override
  protected String getSubject() {
    String lmString =
        DateTimeFormatter.ofPattern("MMMM YYYY")
            .format(
                LocalDateTime.now()
                    .minusMonths(1)
                    .atZone(emailTarget.subscriber().getTimeZoneObj()));
    return lmString + " - Best of Last Month";
  }

  @Override
  protected List<StoryEntity> getStories() {
    var stories = storyRepository.readMonthlyTop();
    return stories;
  }
}
