package com.eguller.hntoplinks.controllers;

import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.models.Navigation;
import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.models.SubscribersContent;
import com.eguller.hntoplinks.models.SubscriptionForm;
import com.eguller.hntoplinks.repository.SubscribersRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestScope
public class SubscribersController {
  private static final String TITLE = "Subscribe to Hacker News Top Links";

  private final SubscribersRepository subscriberRepository;

  public SubscribersController(SubscribersRepository subscriberRepository) {
    this.subscriberRepository = subscriberRepository;
  }

  @GetMapping("/subscribers")
  public String subscribers(Model model) {

    var content = SubscribersContent.builder().build();
    var page = Page.<SubscribersContent>builder()
      .title(TITLE)
      .navigation(Navigation.builder().activeMenu("subscribe").build())
      .content(content)
      .build();
    model.addAttribute("page", page);
    return "subscribe";
  }

  @GetMapping("/subscribers/{subscriberId}")
  public String subscribers(
    Model model,
    @PathVariable("subscriberId") String subscriberId,
    @RequestParam(value = "action") String action) {
    if ("unsubscribe".equals(action)) {
      this.subscriberRepository.deleteBySubscriberId(subscriberId);
      return "unsubscribe";
    } else {
      createModifySubscriptionPage(model, subscriberId);
      return "subscribe";
    }
  }

  private void createModifySubscriptionPage(Model model, String subscriberId) {
    var subscriptionFormBuilder = SubscriptionForm.builder();

    var subscriptionForm = this.subscriberRepository.findBySubscriberId(subscriberId).map(subscriber -> subscriptionFormBuilder
      .subscriberId(subscriber.getSubscriberId())
      .email(subscriber.getEmail())
      .selectedPeriods(subscriber.getSubscriptionList().stream().map(subscription -> subscription.getPeriod()).collect(Collectors.toSet()))
      .build()
    ).orElse(subscriptionFormBuilder.selectedPeriods(Set.of(Period.WEEKLY)).build());

    var content = SubscribersContent.builder()
      .subscriptionForm(subscriptionForm)
      .build();
    var page = Page.<SubscribersContent>builder()
      .title(TITLE)
      .navigation(Navigation.builder().activeMenu("subscribe").build())
      .content(content)
      .build();
    model.addAttribute("page", page);
  }

  @PostMapping("/subscribers")
  public String subscribe(Model model, @ModelAttribute("subscriptionForm") SubscriptionForm subscriptionForm) {

    var content = SubscribersContent.builder().subscriptionForm(subscriptionForm).build();
    var page = Page.<SubscribersContent>builder()
      .title(TITLE)
      .navigation(Navigation.builder().activeMenu("subscribe").build())
      .content(content)
      .build();

    model.addAttribute("page", page);
    model.addAttribute("subscriptionForm", page.getContent().getSubscriptionForm());
    return "subscribe";
  }
}
