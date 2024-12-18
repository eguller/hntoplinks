package com.eguller.hntoplinks.controllers;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.models.Navigation;
import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.models.SubscribersContent;
import com.eguller.hntoplinks.models.SubscriptionForm;
import com.eguller.hntoplinks.repository.SubscribersRepository;
import com.eguller.hntoplinks.repository.SubscriptionsRepository;
import com.eguller.hntoplinks.services.EmailService;
import com.eguller.hntoplinks.services.RecaptchaVerifier;

import jakarta.validation.Valid;

@Controller
@RequestScope
public class SubscribersController {
  private static final String TITLE = "Subscribe to Hacker News Top Links";

  @Value("${hntoplinks.captcha.enabled}")
  private boolean captchaEnabled;

  private final SubscribersRepository subscriberRepository;
  private final SubscriptionsRepository subscriptionsRepository;
  private final RecaptchaVerifier recaptchaVerifier;
  private final EmailService emailService;

  public SubscribersController(
      SubscribersRepository subscribersRepository,
      SubscriptionsRepository subscriptionsRepository,
      RecaptchaVerifier recaptchaVerifier,
      EmailService emailService) {
    this.subscriberRepository = subscribersRepository;
    this.subscriptionsRepository = subscriptionsRepository;
    this.recaptchaVerifier = recaptchaVerifier;
    this.emailService = emailService;
  }

  @GetMapping("/subscribers")
  public String subscribers(Model model) {
    var subscriptionForm = createDefaultSubscriptionForm();
    var content =
        SubscribersContent.builder()
            .captchaEnabled(captchaEnabled)
            .subscriptionForm(subscriptionForm)
            .build();
    var page =
        Page.<SubscribersContent>builder()
            .title(TITLE)
            .navigation(Navigation.builder().activeMenu("subscribe").build())
            .content(content)
            .build();
    model.addAttribute("page", page);
    model.addAttribute("subscriptionForm", page.getContent().getSubscriptionForm());
    return "subscribe";
  }

  private SubscriptionForm createDefaultSubscriptionForm() {
    var subscriptionForm =
        SubscriptionForm.builder().selectedPeriods(Set.of(Period.WEEKLY)).build();
    return subscriptionForm;
  }

  @GetMapping("/subscribers/{subscriberId}")
  public String subscribers(
      Model model,
      @PathVariable("subscriberId") String subscriberId,
      @RequestParam(value = "action", required = false) String action) {
    if ("unsubscribe".equals(action)) {
      var subscriberOpt = this.subscriberRepository.findBySubscriberId(subscriberId);
      subscriberOpt
          .map(SubscriberEntity::getId)
          .ifPresent(subscriptionsRepository::deleteBySubscriberId);
      subscriberOpt.map(SubscriberEntity::getId).ifPresent(subscriberRepository::deleteById);
      createUnsubscribePage(model);
      return "unsubscribe";
    } else {
      createModifySubscriptionPage(model, subscriberId);
      return "subscribe";
    }
  }

  private void createModifySubscriptionPage(Model model, String subscriberId) {
    var subscriptionFormBuilder = SubscriptionForm.builder();

    var subscriptionForm =
        this.subscriberRepository
            .findBySubscriberId(subscriberId)
            .map(
                subscriber ->
                    subscriptionFormBuilder
                        .subscriberId(subscriber.getSubscriberId())
                        .email(subscriber.getEmail())
                        .selectedPeriods(
                            subscriber.getSubscriptionList().stream()
                                .map(subscription -> subscription.getPeriod())
                                .collect(Collectors.toSet()))
                        .build())
            .orElse(subscriptionFormBuilder.selectedPeriods(Set.of(Period.WEEKLY)).build());

    var content =
        SubscribersContent.builder()
            .captchaEnabled(captchaEnabled)
            .subscriptionForm(subscriptionForm)
            .build();
    var page =
        Page.<SubscribersContent>builder()
            .title(TITLE)
            .navigation(Navigation.builder().activeMenu("subscribe").build())
            .content(content)
            .build();
    model.addAttribute("page", page);
    model.addAttribute("subscriptionForm", page.getContent().getSubscriptionForm());
  }

  private void createUnsubscribePage(Model model) {
    var page =
        Page.<SubscribersContent>builder()
            .title("Unsubscribed")
            .navigation(Navigation.builder().build())
            .build();
    model.addAttribute("page", page);
  }

  @PostMapping("/subscribers")
  public String subscribe(
      @Valid @ModelAttribute SubscriptionForm subscriptionForm,
      BindingResult bindingResult,
      Model model) {
    if (captchaEnabled) {
      if (!recaptchaVerifier.verify(subscriptionForm.getCaptchaResponse())) {
        bindingResult.rejectValue("captchaResponse", "recaptcha.invalid", "Invalid captcha");
      }
    }

    if (!StringUtils.hasText(subscriptionForm.getEmail())
        && !Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
            .matcher(subscriptionForm.getEmail())
            .matches()) {
      bindingResult.rejectValue("email", "email.invalid", "Invalid email address");
    }
    var content =
        SubscribersContent.builder()
            .captchaEnabled(captchaEnabled)
            .subscriptionForm(subscriptionForm)
            .build();
    var page =
        Page.<SubscribersContent>builder()
            .title(TITLE)
            .navigation(Navigation.builder().activeMenu("subscribe").build())
            .content(content)
            .build();

    model.addAttribute("page", page);
    model.addAttribute("subscriptionForm", page.getContent().getSubscriptionForm());

    if (bindingResult.hasErrors()) {
      // Handle error
      return "subscribe";
    }

    if (!StringUtils.hasText(subscriptionForm.getSubscriberId())) {
      var subscriber = subscriptionForm.toSubscriberEntity();
      var saveFailed = false;
      final SubscriberEntity savedSubscriber;
      SubscriberEntity tmpSaveSubscriber;
      try {
        tmpSaveSubscriber = subscriberRepository.save(subscriber);
        saveFailed = false;
      } catch (Exception e) {
        tmpSaveSubscriber = subscriber;
        saveFailed = true;
      }

      savedSubscriber = tmpSaveSubscriber;
      subscriptionForm.setSubscriberId(savedSubscriber.getSubscriberId());
      var subscriptions = subscriptionForm.toSubscriptionEntities();
      subscriptions.forEach(subscription -> subscription.setSubscriberId(savedSubscriber.getId()));
      var savedSubscriptions = subscriptions;
      if (!saveFailed) {
        StreamSupport.stream(subscriptionsRepository.saveAll(subscriptions).spliterator(), false)
            .collect(Collectors.toList());
        savedSubscriber.setSubscriptionList(savedSubscriptions);
        emailService.sendSubscriptionEmail(savedSubscriber);
      }
      content.setSuccess(true);
      content.setSuccessMessage("You have successfully subscribed to HN Top Links");
      // save subscribers
    } else {
      // update subscriber
      subscriberRepository
          .findBySubscriberId(subscriptionForm.getSubscriberId())
          .map(
              subscriber -> {
                subscriber.setEmail(subscriptionForm.getEmail());
                subscriberRepository.save(subscriber);
                // update subscriptions
                var subscriptions = subscriptionForm.toSubscriptionEntities();
                subscriptions.forEach(
                    subscription -> subscription.setSubscriberId(subscriber.getId()));
                subscriber.getSubscriptionList().stream()
                    .filter(subscription -> !subscriptions.contains(subscription))
                    .forEach(subscription -> subscriptionsRepository.delete(subscription));
                subscriptionsRepository.saveAll(subscriptions);
                content.setSuccess(true);
                content.setSuccessMessage("Subscription updated successfully");
                return subscriber;
              });
    }
    return "subscribe";
  }
}
