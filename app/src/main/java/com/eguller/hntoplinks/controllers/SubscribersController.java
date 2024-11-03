package com.eguller.hntoplinks.controllers;

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
    return "subscribe";
  }

  @GetMapping("/subscribers/{subscriberId}")
  public String subscribers(
      Model model,
      @PathVariable("subscriberId") String subscriberId,
      @RequestParam(value = "action") String action) {
    if ("unsubscribe".equals(action)) {
      return "unsubscribe";
    }
    return "subscribe";
  }

  @PostMapping("/subscribers")
  public String subscribe(Model model) {
    return "subscribe";
  }

  @PutMapping("subscribe/{subscriberId}")
  public String updateSubscription(Model model, @PathVariable("subscriberId") String subscriberId) {
    return "subscribe";
  }
}
