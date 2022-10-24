package com.eguller.hntoplinks.controllers;

import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.models.PageTab;
import com.eguller.hntoplinks.models.StatsPage;
import com.eguller.hntoplinks.models.StoryPage;
import com.eguller.hntoplinks.models.SubscriptionPage;
import com.eguller.hntoplinks.repository.SubscriberRepository;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import com.eguller.hntoplinks.services.EmailService;
import com.eguller.hntoplinks.services.RecaptchaVerifier;
import com.eguller.hntoplinks.services.StatisticsService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.SubscriptionService;
import com.eguller.hntoplinks.util.DateUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestScope
public class ApplicationController {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final int                 MAX_PAGES      = 10;
  private static final int                 STORY_PER_PAGE = 30;
  private final        HttpServletRequest  httpServletRequest;
  private final        StoryCacheService   storyCacheService;
  private final        SubscriptionService subscriptionService;

  private final StatisticsService statisticsService;

  private final RecaptchaVerifier recaptchaVerifier;

  private final EmailService emailService;

  private final SubscriptionRepository subscriptionRepository;

  private final SubscriberRepository subscriberRepository;


  @Value("${hntoplinks.captcha.enabled}")
  private boolean captchaEnabled;

  public ApplicationController(HttpServletRequest httpServletRequest, StoryCacheService storyCacheService, SubscriptionService subscriptionService, StatisticsService statisticsService, SubscriberRepository subscriberRepository, SubscriptionRepository subscriptionRepository, RecaptchaVerifier recaptchaVerifier, EmailService emailService) {
    this.httpServletRequest     = httpServletRequest;
    this.storyCacheService      = storyCacheService;
    this.subscriptionService    = subscriptionService;
    this.subscriptionRepository = subscriptionRepository;
    this.statisticsService      = statisticsService;
    this.recaptchaVerifier      = recaptchaVerifier;
    this.emailService           = emailService;
  }


  @GetMapping("/")
  public String index(Model model) {
    return today(model, 1);
  }

  @GetMapping("/{page:\\d+}")
  public String index(Model model, @PathVariable(value = "page") Integer page) {
    return today(model, page);
  }

  @GetMapping("/today")
  public String today(Model model) {
    return today(model, 1);
  }

  @GetMapping("/today/{page:\\d+}")
  public String today(Model model, @PathVariable(value = "page") Integer page) {
    var storyPage = getStoryPage(PageTab.today, page);
    model.addAttribute("page", storyPage);
    return view("index");
  }

  @GetMapping("/week")
  public String week(Model model) {
    return week(model, 1);
  }

  @GetMapping("/week/{page:\\d+}")
  public String week(Model model, @PathVariable(value = "page") Integer page) {
    var storyPage = getStoryPage(PageTab.week, page);
    model.addAttribute("page", storyPage);
    return view("index");
  }

  @GetMapping("/month")
  public String month(Model model) {
    return month(model, 1);
  }

  @GetMapping("/month/{page:\\d+}")
  public String month(Model model, @PathVariable(value = "page") Integer page) {
    var storyPage = getStoryPage(PageTab.month, page);
    model.addAttribute("page", storyPage);
    return view("index");
  }

  @GetMapping("/year")
  public String year(Model model) {
    return year(model, 1);
  }

  @GetMapping("/year/{page:\\d+}")
  public String year(Model model, @PathVariable(value = "page") Integer page) {
    var storyPage = getStoryPage(PageTab.year, page);
    model.addAttribute("page", storyPage);
    return view("index");
  }

  @GetMapping("/all")
  public String all(Model model) {
    return all(model, 1);
  }

  @GetMapping("/all/{page:\\d+}")
  public String all(Model model, @PathVariable(value = "page") Integer page) {
    var storyPage = getStoryPage(PageTab.all, page);
    model.addAttribute("page", storyPage);
    return view("index");
  }

  @GetMapping("/about")
  public String about(Model model) {
    var aboutPage = Page.pageBuilder().title("About").build();
    model.addAttribute("page", aboutPage);
    return view("about");
  }

  @GetMapping("/stats")
  public String stats(Model model) {
    var statistics = statisticsService.readStatistics();
    StatsPage statsPage = StatsPage.builder().title("Statistics").statistics(statistics).build();
    model.addAttribute("page", statsPage);
    return view("statistics");
  }

  @GetMapping("/subscribe")
  public String subscribe_Get(Model model, @RequestParam(value = "id", required = false) String subscriptionId) {
    var subscriptionFormBuilder = SubscriptionPage.SubscriptionForm.builder();
    var subscriptionPageBuilder = SubscriptionPage.builder().captchaEnabled(captchaEnabled);
    if (subscriptionId != null) {
      var subscriptionOpt = subscriptionRepository.findBySubsUUID(subscriptionId);
      subscriptionOpt.ifPresent((subscription) -> {
        subscriptionFormBuilder
          .email(subscription.getEmail())
          .daily(subscription.isSubscribedFor(Period.DAILY))
          .weekly(subscription.isSubscribedFor(Period.WEEKLY))
          .monthly(subscription.isSubscribedFor(Period.MONTHLY))
          .yearly(subscription.isSubscribedFor(Period.YEARLY));
      });

    }
    var subscriptionPage = subscriptionPageBuilder.subscriptionForm(subscriptionFormBuilder.build()).build();
    model.addAttribute("page", subscriptionPage);
    return view("subscription");
  }

  @GetMapping("/unsubscribe/{id}")
  public String unsubscribe_Get(Model model, @PathVariable(value = "id") String subscriptionId) {
    var unsubscribePage = Page.pageBuilder().title("Unsubscribe").build();
    var numberOfUsers = subscriptionRepository.deleteBySubsUUID(subscriptionId);
    if (numberOfUsers > 0) {
      statisticsService.userUnsubscribed();
    }
    model.addAttribute("page", unsubscribePage);
    return view("unsubscribe");
  }

  @GetMapping("/update-subscription/{id}")
  public String updateSubscription_Get(Model model, @PathVariable(value = "id") String subscriptionId) {
    var subscriptionForm = SubscriptionPage.SubscriptionForm.builder();
    var subscriptionPageBuilder = SubscriptionPage.builder()
      .title("Update Subscription")
      .captchaEnabled(captchaEnabled);
    var subscriptionOpt = subscriptionRepository.findBySubsUUID(subscriptionId);
    subscriptionOpt.ifPresent((subscription) -> {
      subscriptionForm
        .email(subscription.getEmail())
        .daily(subscription.isSubscribedFor(Period.DAILY))
        .weekly(subscription.isSubscribedFor(Period.WEEKLY))
        .monthly(subscription.isSubscribedFor(Period.MONTHLY))
        .yearly(subscription.isSubscribedFor(Period.YEARLY));
    });

    model.addAttribute("page", subscriptionPageBuilder.subscriptionForm(subscriptionForm.build()).build());
    return view("subscription");
  }


  @PostMapping("/subscribe")
//  public String subscribe_Post(@ModelAttribute SubscriptionPage.SubscriptionForm subscriptionForm, @ModelAttribute("g-recaptcha-response") String recaptchaResponse, Model model) {
  public String subscribe_Post(@ModelAttribute SubscriptionPage.SubscriptionForm subscriptionForm, Model model) {
    var subscriptionPageBuilder = SubscriptionPage.builder();
    subscriptionPageBuilder.subscriptionForm(subscriptionForm);
    subscriptionPageBuilder.captchaEnabled(captchaEnabled);
    var hasError = false;
    if (!StringUtils.hasLength(subscriptionForm.getEmail())) {
      subscriptionPageBuilder.error("Email address can not be empty.");
      hasError = true;
    } else if (!EmailValidator.getInstance().isValid(subscriptionForm.getEmail())) {
      subscriptionPageBuilder.error("Email address is not valid.");
      hasError = true;
    }

    var isCaptchaValid = recaptchaVerifier.verify(subscriptionForm.getGRecaptchaResponse());

    if (!isCaptchaValid) {
      subscriptionPageBuilder.error("I'm not a robot check has failed.");
      hasError = true;
    }

    if (!subscriptionForm.hasSubscription()) {
      subscriptionPageBuilder.error("Please select at least one of the daily, weekly, monthly or annually subscriptions.");
      hasError = true;
    }

    if (hasError) {
      model.addAttribute("page", subscriptionPageBuilder.build());
      return view("subscription");
    }

    var subscriber = subscriberRepository.find(subscriptionForm.getEmail().toLowerCase()).or(() -> {
      var newSubscriber = new SubscriberEntity();
      newSubscriber.setTimeZone(subscriptionForm.getGRecaptchaResponse());
      newSubscriber.setActivated(true);
      newSubscriber.setSubsUUID(UUID.randomUUID().toString());
      newSubscriber.setEmail(subscriptionForm.getEmail());
      newSubscriber.setSubscriptionDate(LocalDate.now());
      return Optional.of(newSubscriber);
    }).get();

    if (subscriber.getSubsUUID().equalsIgnoreCase(subscriptionForm.getSubsUUID()) || subscriptionForm.getSubsUUID() == null) {
      var dailySubscription = subscriber.getSubscriptionList().stream().filter(subscription -> Period.DAILY == subscription.getPeriod()).findFirst();
      var weeklySubscription = subscriber.getSubscriptionList().stream().filter(subscription -> Period.WEEKLY == subscription.getPeriod()).findFirst();
      var monthlySubscription = subscriber.getSubscriptionList().stream().filter(subscription -> Period.MONTHLY == subscription.getPeriod()).findFirst();
      var yearlySubscription = subscriber.getSubscriptionList().stream().filter(subscription -> Period.YEARLY == subscription.getPeriod()).findFirst();

      if (dailySubscription.isPresent() && !subscriptionForm.isDaily()) {
        subscriber.getSubscriptionList().remove(dailySubscription);
      } else if (dailySubscription.isEmpty() && subscriptionForm.isDaily()) {
        var subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setSubscriber(subscriber);
        subscriptionEntity.setPeriod(Period.DAILY);
        subscriptionEntity.setNextSendDate(DateUtils.tomorrow_7_AM(subscriber.getTimeZoneObj()));
        subscriber.getSubscriptionList().add(subscriptionEntity);
      }

      if (weeklySubscription.isPresent() && !subscriptionForm.isWeekly()) {
        subscriber.getSubscriptionList().remove(weeklySubscription);
      } else if (weeklySubscription.isEmpty() && subscriptionForm.isWeekly()) {
        var subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setSubscriber(subscriber);
        subscriptionEntity.setPeriod(Period.WEEKLY);
        subscriptionEntity.setNextSendDate(DateUtils.nextMonday_7_AM(subscriber.getTimeZoneObj()));
        subscriber.getSubscriptionList().add(subscriptionEntity);
      }

      if (monthlySubscription.isPresent() && !subscriptionForm.isMonthly()) {
        subscriber.getSubscriptionList().remove(monthlySubscription);
      } else if (monthlySubscription.isEmpty() && subscriptionForm.isMonthly()) {
        var subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setSubscriber(subscriber);
        subscriptionEntity.setPeriod(Period.MONTHLY);
        subscriptionEntity.setNextSendDate(DateUtils.firstDayOfNextMonth_7_AM(subscriber.getTimeZoneObj()));
        subscriber.getSubscriptionList().add(subscriptionEntity);
      }

      if (yearlySubscription.isPresent() && !subscriptionForm.isYearly()) {
        subscriber.getSubscriptionList().remove(yearlySubscription);
      } else if (yearlySubscription.isEmpty() && subscriptionForm.isYearly()) {
        var subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setSubscriber(subscriber);
        subscriptionEntity.setPeriod(Period.YEARLY);
        subscriptionEntity.setNextSendDate(DateUtils.firstDayOfNextYear_7_AM(subscriber.getTimeZoneObj()));
        subscriber.getSubscriptionList().add(subscriptionEntity);
      }
    }

    if (subscriptionForm.getSubsUUID() == null) {
      subscriptionPageBuilder.message("You have subscribed.");
    } else {
      subscriptionPageBuilder.message("Subscription has been updated.");
    }
    model.addAttribute("page",subscriptionPageBuilder.build());
    return view("subscription");
  }

  private StoryPage getStoryPage(PageTab pageTab, Integer page) {
    var _page = page == null ? 1 : page;
    List<Story> storyList;
    if (PageTab.today == pageTab) {
      storyList = storyCacheService.getDailyTop();
    } else if (PageTab.week == pageTab) {
      storyList = storyCacheService.getWeeklTop();
    } else if (PageTab.month == pageTab) {
      storyList = storyCacheService.getMonthlyTop();
    } else if (PageTab.year == pageTab) {
      storyList = storyCacheService.getYearlyTop();
    } else {
      storyList = storyCacheService.getAllTimeTop();
    }

    int from = Math.min(storyList.size() - 1, (_page - 1) * STORY_PER_PAGE);
    from = Math.max(0, from);
    int to = Math.min(storyList.size(), _page * STORY_PER_PAGE);
    to = Math.max(0, to);
    var viewList = storyList.subList(from, to);
    boolean hasMoreStories = storyList.size() - 1 > to;
    StoryPage storyPage = StoryPage.builder()
      .title(pageTab.getTitleText())
      .activeTab(pageTab)
      .currentPage(_page)
      .storyList(viewList)
      .hasMoreStories(hasMoreStories)
      .storyPerPage(STORY_PER_PAGE)
      .build();
    return storyPage;
  }

  private String view(String view) {
    Device device = DeviceUtils.getCurrentDevice(httpServletRequest);
    if (device == null || device.isNormal()) {
      return view;
    } else {
      return view + "_mobile";
    }
  }

  public int getPage(String pageStr) {
    try {
      int page = Integer.parseInt(pageStr);
      if (page < 1) {
        return 1;
      } else if (page > MAX_PAGES) {
        return MAX_PAGES;
      } else {
        return page;
      }

    } catch (Exception ex) {
      logger.error("Page could not be parse. pageStr={}", pageStr);
    }
    return 1;
  }
}
