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

public class DailySubscriptionEmailTask extends SubscriptionEmailTask {
  private ItemsRepository itemRepository;

  public DailySubscriptionEmailTask(
      TemplateService templateService,
      EmailTarget emailTarget,
      EmailProviderService emailProviderService,
      ItemsRepository itemRepository) {
    super(templateService, emailTarget, emailProviderService);
    this.itemRepository = itemRepository;
  }

  @Override
  protected LocalDateTime getNextSendDate() {
    var nextSendDate = DateUtils.tomorrow_7_AM(emailTarget.subscriber().getTimeZoneObj());
    return nextSendDate;
  }

  @Override
  protected String getSubject() {
    String timePrefix =
        DateTimeFormatter.ofPattern("EEEE, dd MMMM")
            .format(
                LocalDateTime.now().minusDays(1).atZone(emailTarget.subscriber().getTimeZoneObj()));
    return timePrefix + " - Daily Top Links";
  }

  @Override
  protected List<Item> getStories() {
    var intervalForYesterday = DateUtils.getIntervalForYesterday();
    var stories = itemRepository.findByInterval(intervalForYesterday, SortType.UPVOTES, getMaxStoryCount(), 0);
    return stories;
  }
}
