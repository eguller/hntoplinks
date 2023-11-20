package com.eguller.hntoplinks.controllers;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.models.PageTab;
import com.eguller.hntoplinks.models.StatsPage;
import com.eguller.hntoplinks.models.StoryPage;
import com.eguller.hntoplinks.models.SubscriptionForm;
import com.eguller.hntoplinks.models.SubscriptionPage;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.repository.SubscriberRepository;
import com.eguller.hntoplinks.repository.SubscriptionRepository;
import com.eguller.hntoplinks.services.EmailService;
import com.eguller.hntoplinks.services.RecaptchaVerifier;
import com.eguller.hntoplinks.services.StatisticsService;
import com.eguller.hntoplinks.services.SubscriptionService;
import com.eguller.hntoplinks.springframework.mobile.device.DeviceUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Controller
@RequestScope
public class ApplicationController {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final int                STORY_PER_PAGE = 30;
  private final        HttpServletRequest httpServletRequest;

  private final StatisticsService statisticsService;

  private final RecaptchaVerifier recaptchaVerifier;

  private final EmailService emailService;

  private final SubscriptionRepository subscriptionRepository;

  private final SubscriberRepository subscriberRepository;

  private final StoryRepository storyRepository;


  @Value("${hntoplinks.captcha.enabled}")
  private boolean captchaEnabled;

  public ApplicationController(HttpServletRequest httpServletRequest, SubscriptionService subscriptionService, StatisticsService statisticsService, SubscriberRepository subscriberRepository, SubscriptionRepository subscriptionRepository, RecaptchaVerifier recaptchaVerifier, EmailService emailService, StoryRepository storyRepository) {
    this.httpServletRequest     = httpServletRequest;
    this.subscriptionRepository = subscriptionRepository;
    this.subscriberRepository   = subscriberRepository;
    this.statisticsService      = statisticsService;
    this.recaptchaVerifier      = recaptchaVerifier;
    this.emailService           = emailService;
    this.storyRepository        = storyRepository;
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
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
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
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
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
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
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
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
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
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
    return view("index");
  }

  @GetMapping("/about")
  public String about(Model model) {
    var aboutPage = Page.pageBuilder().title("About").build();
    model.addAttribute("page", aboutPage);
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
    return view("about");
  }

  @GetMapping("/stats")
  public String stats(Model model) {
    var statistics = statisticsService.readStatistics();
    StatsPage statsPage = StatsPage.builder().title("Statistics").statistics(statistics).build();
    model.addAttribute("page", statsPage);
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
    return view("statistics");
  }

  @GetMapping("/subscribe")
  public String subscribe_Get(Model model, @RequestParam(value = "id", required = false) String subscriptionId) {
    var subscriptionFormBuilder = SubscriptionForm.builder();
    var subscriptionPageBuilder = SubscriptionPage.builder().captchaEnabled(captchaEnabled);
    if (subscriptionId != null) {
      var subscriberOpt = subscriberRepository.findBySubsUUID(subscriptionId);
      subscriberOpt.ifPresent((subscriber) -> {
        subscriptionFormBuilder
          .email(subscriber.getEmail())
          .selectedPeriods(
            subscriber.getSubscriptionList().stream()
              .map(subscription -> subscription.getPeriod()).collect(Collectors.toSet())
          );
      });

    }
    var subscriptionPage = subscriptionPageBuilder.subscriptionForm(subscriptionFormBuilder.build()).build();
    model.addAttribute("page", subscriptionPage);
    model.addAttribute("subscriptionForm", subscriptionPage.getSubscriptionForm());
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
    return view("subscription");
  }

  @GetMapping("/unsubscribe/{id}")
  public String unsubscribe_Get(Model model, @PathVariable(value = "id") String subscriptionId) {
    var unsubscribePage = Page.pageBuilder().title("Unsubscribe").build();
    var subscriberEntityOptional = subscriberRepository.findBySubsUUID(subscriptionId);
    subscriberEntityOptional.ifPresent(subscriber -> subscriberRepository.delete(subscriber));

    model.addAttribute("page", unsubscribePage);
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
    return view("unsubscribe");
  }

  @GetMapping("/update-subscription/{id}")
  public String updateSubscription_Get(Model model, @PathVariable(value = "id") String subscriptionId) {
    var subscriptionFormBuilder = SubscriptionForm.builder();
    var subscriptionPageBuilder = SubscriptionPage.builder()
      .title("Update Subscription")
      .captchaEnabled(captchaEnabled);
    var subscriberOpt = subscriberRepository.findBySubsUUID(subscriptionId);
    subscriberOpt.ifPresent((subscriber) -> {
      subscriptionFormBuilder
        .email(subscriber.getEmail())
        .subsUUID(subscriber.getSubsUUID())
        .selectedPeriods(
          subscriber.getSubscriptionList().stream()
            .map(subscription -> subscription.getPeriod()).collect(Collectors.toSet())
        );
    });

    var subscriptionPage = subscriptionPageBuilder.subscriptionForm(subscriptionFormBuilder.build()).build();
    model.addAttribute("page", subscriptionPage);
    model.addAttribute("subscriptionForm", subscriptionPage.getSubscriptionForm());
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());
    return view("subscription");
  }


  @PostMapping("/subscribe")
  public String subscribe_Post(@ModelAttribute("subscriptionForm") SubscriptionForm subscriptionForm, Model model) {
    model.addAttribute("httpServletRequest.requestURI", httpServletRequest.getRequestURI());

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

    if (subscriptionForm.getSelectedPeriods().isEmpty()) {
      subscriptionPageBuilder.error("Please select at least one of the daily, weekly, monthly or annually subscriptions.");
      hasError = true;
    }

    if (hasError) {
      model.addAttribute("page", subscriptionPageBuilder.build());
      return view("subscription");
    }

    var subscriber = subscriberRepository.findByEmail(subscriptionForm.getEmail().toLowerCase()).or(() -> {
      var newSubscriber = new SubscriberEntity();
      newSubscriber.setTimeZone(subscriptionForm.getTimeZone());
      newSubscriber.setActivated(true);
      newSubscriber.setActivationDate(LocalDateTime.now());

      newSubscriber.setEmail(subscriptionForm.getEmail());
      newSubscriber.setSubscriptionDate(LocalDateTime.now());

      var subscriberUUID = UUID.randomUUID().toString();
      newSubscriber.setSubsUUID(subscriberUUID);
      return Optional.of(newSubscriber);
    }).get();

    if (subscriber.isNew()) {
      subscriptionPageBuilder.message("You have subscribed.");
      subscriptionForm.setSubsUUID(subscriber.getSubsUUID());
      emailService.sendSubscriptionEmail(subscriber);
    } else {
      subscriptionPageBuilder.message("Subscription has been updated.");
      if (!subscriber.getSubsUUID().equalsIgnoreCase(subscriptionForm.getSubsUUID())) {
        model.addAttribute("page", subscriptionPageBuilder.build());
        return view("subscription");
      }
    }

    subscriberRepository.save(subscriber);

    //find removed subscriptions
    var existingSubscriptions = subscriber.getSubscriptionList()
      .stream()
      .filter(subscription -> subscriptionForm.getSelectedPeriods().contains(subscription.getPeriod()))
      .toList();


    var newSubscriptions = subscriptionForm.getSelectedPeriods()
      .stream()
      .filter(period -> !subscriber.hasSubscription(period))
      .map(period -> subscriber.createNewSubscription(period))
      .map(subscriptionEntity -> {
        subscriptionEntity.setSubscriberId(subscriber.getId());
        return subscriptionEntity;
      })
      .toList();

    var subscriptions = Stream.concat(existingSubscriptions.stream(), newSubscriptions.stream());
    var subscriptionsList = subscriptions.map(subscriptionEntity -> {
      subscriptionRepository.save(subscriptionEntity);
      return subscriptionEntity;
    }).toList();

    var deletedSubscriptions = subscriber.getSubscriptionList().stream()
      .filter(subscriptionEntity -> !subscriptionForm.getSelectedPeriods().contains(subscriptionEntity.getPeriod()));
    deletedSubscriptions.forEach(deletedSubscription -> subscriptionRepository.delete(deletedSubscription));

    subscriber.setSubscriptionList(subscriptionsList);


    model.addAttribute("page", subscriptionPageBuilder.build());
    return view("subscription");
  }

  private StoryPage getStoryPage(PageTab pageTab, Integer page) {
    var _page = page == null ? 1 : page;
    List<StoryEntity> storyList;
    if (PageTab.today == pageTab) {
      storyList = storyRepository.readDailyTop();
    } else if (PageTab.week == pageTab) {
      storyList = storyRepository.readWeeklyTop();
    } else if (PageTab.month == pageTab) {
      storyList = storyRepository.readMonthlyTop();
    } else if (PageTab.year == pageTab) {
      storyList = storyRepository.readyAnnuallyTop();
    } else {
      storyList = storyRepository.readAllTimeTop();
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
}
