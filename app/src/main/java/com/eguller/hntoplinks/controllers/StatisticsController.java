package com.eguller.hntoplinks.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eguller.hntoplinks.models.Navigation;
import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.repository.SubscribersRepository;

@Controller
public class StatisticsController {

  private final SubscribersRepository subscribersRepository;

  public StatisticsController(SubscribersRepository subscribersRepository) {
    this.subscribersRepository = subscribersRepository;
  }

  @GetMapping("/statistics")
  public String statistics(Model model) {
    long activeSubscribers = subscribersRepository.countByActivatedIsTrue();

    Page<Long> page =
        Page.<Long>builder()
            .title("Statistics - HN Top Links")
            .navigation(Navigation.builder().activeMenu("statistics").build())
            .content(activeSubscribers)
            .build();

    model.addAttribute("page", page);
    return "statistics";
  }
}
