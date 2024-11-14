package com.eguller.hntoplinks.services.subscription;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.entities.SortType;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.ItemsRepository;
import com.eguller.hntoplinks.services.EmailProviderService;
import com.eguller.hntoplinks.services.TemplateService;
import com.eguller.hntoplinks.util.DateUtils;

public class MonthlySubscriptionEmailTask extends SubscriptionEmailTask {
  private final ItemsRepository itemsRepository;

  public MonthlySubscriptionEmailTask(
      TemplateService templateService,
      EmailTarget emailTarget,
      EmailProviderService emailProviderService,
      ItemsRepository itemsRepository) {
    super(templateService, emailTarget, emailProviderService);
    this.itemsRepository = itemsRepository;
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
  protected List<Item> getItems() {
    var interval = DateUtils.getIntervalForLastMonth();
    var items = itemsRepository.findByInterval(interval, SortType.UPVOTES, getMaxStoryCount(), 0);
    return items;
  }
}
