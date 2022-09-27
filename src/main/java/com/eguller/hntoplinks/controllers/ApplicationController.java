package com.eguller.hntoplinks.controllers;

import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.models.PageTab;
import com.eguller.hntoplinks.models.StatsPage;
import com.eguller.hntoplinks.models.Story;
import com.eguller.hntoplinks.models.StoryPage;
import com.eguller.hntoplinks.models.Subscription;
import com.eguller.hntoplinks.models.SubscriptionForm;
import com.eguller.hntoplinks.models.SubscriptionPage;
import com.eguller.hntoplinks.services.EmailService;
import com.eguller.hntoplinks.services.RecaptchaVerifier;
import com.eguller.hntoplinks.services.StatisticsService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.SubscriptionService;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;


@Controller
@RequestScope
public class ApplicationController {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final int                 MAX_PAGES      = 10;
  private static final int                 STORY_PER_PAGE = 30;
  @Autowired
  private              HttpServletRequest  httpServletRequest;
  @Autowired
  private              StoryCacheService   storyCacheService;
  @Autowired
  private              SubscriptionService subscriptionService;

  @Autowired
  private StatisticsService statisticsService;

  @Autowired
  private RecaptchaVerifier recaptchaVerifier;

  @Autowired
  private EmailService emailService;

  @Value("${hntoplinks.captcha.enabled}")
  private boolean captchaEnabled;


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
    var subscriptionPage = Optional.ofNullable(subscriptionId)
      .flatMap(id -> subscriptionService.findBySubscriptionId(id))
      .or(() -> Optional.of(Subscription.NEW))
      .map(subscription -> SubscriptionPage.builder().captchaEnabled(captchaEnabled).subscription(subscription).build())
      .get();

    model.addAttribute("page", subscriptionPage);
    return view("subscription");
  }

  @GetMapping("/unsubscribe/{id}")
  public String unsubscribe_Get(Model model, @PathVariable(value = "id") String subscriptionId) {
    var unsubscribePage = Page.pageBuilder().title("Unsubscribe").build();
    var isUnsubscribed = subscriptionService.unsubscribe(subscriptionId);
    if (isUnsubscribed) {
      statisticsService.userUnsubscribed();
    }
    model.addAttribute("page", unsubscribePage);
    return view("unsubscribe");
  }

  @GetMapping("/update-subscription/{id}")
  public String updateSubscription_Get(Model model, @PathVariable(value = "id") String subscriptionId) {
    var subscription = subscriptionService.findBySubscriptionId(subscriptionId);
    var subscriptionPageBuilder = SubscriptionPage.builder().title("Update Subscription").subscription(subscription.orElse(null));

    model.addAttribute("page", subscriptionPageBuilder.build());
    return view("subscription");
  }


  @PostMapping("/subscribe")
  public String subscribe_Post(@ModelAttribute SubscriptionForm subscriptionForm, @ModelAttribute("g-recaptcha-response") String recaptchaResponse, Model model) {
    var subscriptionPageBuilder = SubscriptionPage.builder();
    var subscription = subscriptionForm.getSubscription();
    subscriptionPageBuilder.subscription(subscription);
    subscriptionPageBuilder.captchaEnabled(captchaEnabled);
    var hasError = false;
    if (!StringUtils.hasLength(subscription.getEmail())) {
      subscriptionPageBuilder.error("Email address can not be empty.");
      hasError = true;
    } else if (!EmailValidator.getInstance().isValid(subscription.getEmail())) {
      subscriptionPageBuilder.error("Email address is not valid.");
      hasError = true;
    }

    var isCaptchaValid = recaptchaVerifier.verify(recaptchaResponse);

    if(!isCaptchaValid){
      subscriptionPageBuilder.error("I'm not a robot check has failed.");
      hasError = true;
    }

    if (!subscription.hasSubscription()) {
      subscriptionPageBuilder.error("Please select at least one of the daily, weekly, monthly or annually subscriptions.");
      hasError = true;
    }

    if (hasError) {
      model.addAttribute("page", subscriptionPageBuilder.build());
      return view("subscription");
    }

    subscriptionService.findByEmail(subscription.getEmail().toLowerCase())
      .ifPresentOrElse(existingSubscription -> {
          if (existingSubscription.getSubsUUID().equalsIgnoreCase(subscription.getSubsUUID())) {
            var updatedSubscription = subscriptionService.save(subscription);
            subscriptionPageBuilder.subscription(updatedSubscription);
          }
          //if id and email does not match, print message but do not do anything.
          subscriptionPageBuilder.message("Subscription has been updated.");
        },
        () -> {
          subscription.setSubsUUID(null);
          var savedSubscription = subscriptionService.save(subscription);
          subscriptionPageBuilder.message("You have subscribed.");
          subscriptionPageBuilder.subscription(savedSubscription);
          emailService.sendSubscriptionEmail(savedSubscription);
          statisticsService.userSubscribed();
          if (savedSubscription.isDaily()) {
            statisticsService.userSubscribedForDaily();
          }
          if (savedSubscription.isWeekly()) {
            statisticsService.userSubscribedForWeekly();
          }
          if (savedSubscription.isMonthly()) {
            statisticsService.userSubscribedForMonthly();
          }
          if (savedSubscription.isAnnually()) {
            statisticsService.userSubscribedForAnnually();
          }
        }
      );

    model.addAttribute("page", subscriptionPageBuilder.build());
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
      storyList = storyCacheService.getAnnuallyTop();
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
