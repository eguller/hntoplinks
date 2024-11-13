package com.eguller.hntoplinks.services.subscription;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.ItemsRepository;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.TemplateService;
import com.eguller.hntoplinks.util.DateUtils;

public class YearlySubscriptionEmailTask extends SubscriptionEmailTask {
  private final ItemsRepository itemsRepository;

  public YearlySubscriptionEmailTask(
      TemplateService templateService,
      EmailTarget emailTarget,
      EmailProviderService emailProviderService,
      ItemsRepository itemsRepository) {
    super(templateService, emailTarget, emailProviderService);
    this.itemsRepository = itemsRepository;
  }

  @Override
  protected LocalDateTime getNextSendDate() {
    var nextSendDate = DateUtils.firstDayOfNextYear_7_AM(emailTarget.subscriber().getTimeZoneObj());
    return nextSendDate;
  }

  @Override
  protected String getSubject() {
    String lastYear =
        DateTimeFormatter.ofPattern("YYYY")
            .format(
                LocalDateTime.now()
                    .minusYears(1)
                    .atZone(emailTarget.subscriber().getTimeZoneObj()));
    return "Best of " + lastYear;
  }

  @Override
  protected List<Item> getStories() {
    var stories = this.itemsRepository.readyAnnuallyTop();
    return stories;
  }
}
