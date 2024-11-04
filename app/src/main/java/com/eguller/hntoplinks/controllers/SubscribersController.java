package com.eguller.hntoplinks.controllers;

import com.eguller.hntoplinks.models.Navigation;
import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.models.StoriesContent;
import com.eguller.hntoplinks.models.SubscribersContent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

@Controller
@RequestScope
public class SubscribersController {
  @GetMapping("/subscribers")
  public String subscribers(Model model) {

    var content = SubscribersContent.builder().build();
    var page = Page.<SubscribersContent>builder()
      .title("Subscribe to Hacker News Top Links")
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

    var content = SubscribersContent.builder().build();
    var page = Page.<SubscribersContent>builder()
      .title("Subscribe to Hacker News Top Links")
      .navigation(Navigation.builder().activeMenu("subscribe").build())
      .content(content)
      .build();

    model.addAttribute("page", page);
    if ("unsubscribe".equals(action)) {
      return "unsubscribe";
    }
    return "subscribe";
  }

  @PostMapping("/subscribers")
  public String subscribe(Model model) {

    var content = SubscribersContent.builder().build();
    var page = Page.<SubscribersContent>builder()
      .title("Subscribe to Hacker News Top Links")
      .navigation(Navigation.builder().activeMenu("subscribe").build())
      .content(content)
      .build();

    model.addAttribute("page", page);
    return "subscribe";
  }

  @PutMapping("subscribe/{subscriberId}")
  public String updateSubscription(Model model, @PathVariable("subscriberId") String subscriberId) {
    var content = SubscribersContent.builder().build();
    var page = Page.<SubscribersContent>builder()
      .title("Subscribe to Hacker News Top Links")
      .navigation(Navigation.builder().activeMenu("subscribe").build())
      .content(content)
      .build();

    model.addAttribute("page", page);
    return "subscribe";
  }
}
