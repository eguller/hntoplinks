package com.eguller.hntoplinks.controllers;

import com.eguller.hntoplinks.models.Breadcrumb;
import com.eguller.hntoplinks.models.Interval;
import com.eguller.hntoplinks.models.Navigation;
import com.eguller.hntoplinks.models.Page2;
import com.eguller.hntoplinks.models.StoriesContent;
import com.eguller.hntoplinks.repository.ItemRepository;
import com.eguller.hntoplinks.util.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestScope
public class Application2Controller {
  private static final int            PAGE_SIZE = 30;
  private final        ItemRepository itemRepository;

  public Application2Controller(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  @GetMapping("/v2")
  public String index(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
    var interval = DateUtils.getIntervalForToday();
    var items = itemRepository.findByIntervalSortByScore(interval, PAGE_SIZE, page);
    var storiesContent = StoriesContent.builder()
      .stories(items)
      .sortBy("upvotes")
      .currentPage(page)
      .totalPages(items.size() < PAGE_SIZE ? page : page + 1)
      .build();

    var navigation = Navigation.builder()
      .activeMenu("day")
      .build();

    var pageModel = Page2.builder()
      .title("Today")
      .navigation(navigation)
      .content(storiesContent)
      .build();
    model.addAttribute("page", pageModel);
    return "index2";
  }

  @GetMapping("/v2/today")
  public String today(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
    var interval = DateUtils.getIntervalForToday();
    var items = itemRepository.findByIntervalSortByScore(interval, PAGE_SIZE, page);
    return "index2";
  }

  @GetMapping("/v2/week")
  public String week(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
    var interval = DateUtils.getIntervalForCurrentWeek();
    var items = itemRepository.findByIntervalSortByScore(interval, PAGE_SIZE, page);
    return "index2";
  }


  @GetMapping("/v2/month")
  public String month(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
    var interval = DateUtils.getIntervalForCurrentMonth();
    var items = itemRepository.findByIntervalSortByScore(interval, PAGE_SIZE, page);
    return "index2";
  }

  @GetMapping("/v2/year")
  public String year(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
    var interval = DateUtils.getIntervalForToday();
    var items = itemRepository.findByIntervalSortByScore(interval, PAGE_SIZE, page);
    var storiesContent = StoriesContent.builder()
      .stories(items)
      .sortBy("upvotes")
      .currentPage(page)
      .totalPages(items.size() < PAGE_SIZE ? page : page + 1)
      .build();

    var navigation = Navigation.builder()
      .activeMenu("day")
      .build();

    var pageModel = Page2.builder()
      .title("Today")
      .navigation(navigation)
      .content(storiesContent)
      .build();
    model.addAttribute("page", pageModel);
    return "index2";
  }

  @GetMapping("/v2/all")
  public String all(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
    var items = itemRepository.findAllSortByScore(PAGE_SIZE, page);
    var storiesContent = StoriesContent.builder()
      .stories(items)
      .sortBy("upvotes")
      .currentPage(page)
      .totalPages(items.size() < PAGE_SIZE ? page : page + 1)
      .build();

    var navigation = Navigation.builder()
      .activeMenu("all")
      .build();

    var pageModel = Page2.builder()
      .title("All Time")
      .navigation(navigation)
      .content(storiesContent)
      .build();
    model.addAttribute("page", pageModel);
    return "index2";
  }

  @GetMapping("/v2/stories/{year:\\d}")
  public String byYear(Model model, @PathVariable int year, @RequestParam(value = "page", defaultValue = "1") int page) {
    var interval = DateUtils.getInterval(year);
    var items = itemRepository.findByIntervalSortByScore(interval, PAGE_SIZE, page);
    return "index2";
  }

  @GetMapping("/v2/stories/{year:\\d}/{month:\\d}")
  public String byMonth(Model model, @PathVariable int year, @PathVariable int month, @RequestParam(value = "page", defaultValue = "1") int page) {
    var interval = DateUtils.getInterval(year);
    var items = itemRepository.findByIntervalSortByScore(interval, PAGE_SIZE, page);
    return "index2";
  }

  @GetMapping("/v2/stories/{year:\\d}/{month:\\d}/{day:\\d}")
  public String byDay(Model model, @PathVariable("year") int year, @PathVariable("month") int month, @PathVariable("day") int day, @RequestParam(value = "page", defaultValue = "1") int page) {
    return "index2";
  }

  @GetMapping("/v/stories")
  public String stories(Model model, @RequestParam("from") String from, @RequestParam("to") String to, @RequestParam(value = "page", defaultValue = "1") int page) {
    try {
      var fromDate = LocalDate.parse(from).atStartOfDay();
      var toDate = LocalDate.parse(to).atTime(LocalTime.MAX);
      var items = itemRepository.findByIntervalSortByScore(new Interval(fromDate, toDate), page);
      return "index2";
    } catch (Exception ex) {
      return index(model, 1);
    }
  }
}
