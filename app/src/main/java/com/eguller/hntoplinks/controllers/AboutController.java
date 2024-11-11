package com.eguller.hntoplinks.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eguller.hntoplinks.models.Navigation;
import com.eguller.hntoplinks.models.Page;

@Controller
public class AboutController {
  @GetMapping("/about")
  public String about(Model model) {
    Page<Void> page =
        Page.<Void>builder()
            .title("About HN Top Links")
            .navigation(Navigation.builder().activeMenu("about").build())
            .build();

    model.addAttribute("page", page);
    return "about";
  }
}
