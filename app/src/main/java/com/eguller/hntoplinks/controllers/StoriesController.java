package com.eguller.hntoplinks.controllers;

import java.time.LocalDate;
import java.time.LocalTime;

import com.eguller.hntoplinks.util.StoriesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import com.eguller.hntoplinks.entities.SortType;
import com.eguller.hntoplinks.models.Interval;
import com.eguller.hntoplinks.models.Navigation;
import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.models.StoriesContent;
import com.eguller.hntoplinks.repository.ItemsRepository;
import com.eguller.hntoplinks.util.DateUtils;

@Controller
@RequestScope
public class StoriesController {
  private final ItemsRepository itemRepository;

  public StoriesController(ItemsRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  @GetMapping("/")
  public String index(
    Model model,
    @RequestParam(value = "year", required = false) Integer year,
    @RequestParam(value = "month", required = false) Integer month,
    @RequestParam(value = "week", required = false) Integer week,
    @RequestParam(value = "day", required = false) Integer day,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var pageBuilder = Page.builder();
    if (year != null && month != null && day != null) {
      today(model, page, sort);
    } else if (year != null & month != null) {
      today(model, page, sort);
    } else if (year != null && week != null) {
      today(model, page, sort);
    } else if (year != null) {
      today(model, page, sort);
    } else {
      today(model, page, sort);
    }
    return "index";
  }

  @GetMapping("/today")
  public String today(
    Model model,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getIntervalForToday();
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().activeMenu("day").build();

    var pageModel =
      Page.builder()
        .title("Best of today")
        .currentPath("/today")
        .navigation(navigation)
        .content(storiesContent)
        .build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/week")
  public String week(
    Model model,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getIntervalForCurrentWeek();
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().activeMenu("week").build();

    var pageModel =
      Page.builder()
        .title("Best of the week")
        .currentPath("/week")
        .navigation(navigation)
        .content(storiesContent)
        .build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/month")
  public String month(
    Model model,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getIntervalForCurrentMonth();
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().activeMenu("month").build();

    var pageModel =
      Page.builder()
        .title("Best of the  month")
        .currentPath("/month")
        .navigation(navigation)
        .content(storiesContent)
        .build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/year")
  public String year(
    Model model,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getIntervalForToday();
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().activeMenu("year").build();

    var pageModel =
      Page.builder()
        .title("Best of %d".formatted(LocalDate.now().getYear()))
        .currentPath("/year")
        .navigation(navigation)
        .content(storiesContent)
        .build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/all")
  public String all(
    Model model,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var items = itemRepository.findAll(StoriesUtils.PAGE_SIZE, sort, page);
    var storiesContent =
      StoriesContent.builder()
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().activeMenu("all").build();

    var pageModel =
      Page.builder()
        .title("Best of All Time")
        .currentPath("/all")
        .navigation(navigation)
        .content(storiesContent)
        .build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/stories/{year:\\d}")
  public String byYear(
    Model model,
    @PathVariable int year,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getInterval(year);
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().activeMenu("all").build();

    var pageModel =
      Page.builder().title("All Time").navigation(navigation).content(storiesContent).build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/stories/{year:\\d}/{month:\\d}")
  public String byMonth(
    Model model,
    @PathVariable int year,
    @PathVariable int month,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getInterval(year);
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().build();

    var pageModel =
      Page.builder().title("All Time").navigation(navigation).content(storiesContent).build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/stories/{year:\\d}/{month:\\d}/{day:\\d}")
  public String byDay(
    Model model,
    @PathVariable("year") int year,
    @PathVariable("month") int month,
    @PathVariable("day") int day,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getInterval(year, month, day);

    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().build();

    var pageModel =
      Page.builder().title("All Time").navigation(navigation).content(storiesContent).build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/stories")
  public String stories(
    Model model,
    @RequestParam("from") String from,
    @RequestParam("to") String to,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    try {
      var fromDate = LocalDate.parse(from).atStartOfDay();
      var toDate = LocalDate.parse(to).atTime(LocalTime.MAX);
      var items = itemRepository.findByInterval(new Interval(fromDate, toDate), page);
      var storiesContent =
        StoriesContent.builder()
          .stories(items)
          .sortBy(sort)
          .currentPage(page)
          .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
          .build();

      var navigation = Navigation.builder().build();

      var pageModel =
        Page.builder().title("All Time").navigation(navigation).content(storiesContent).build();
      model.addAttribute("page", pageModel);
      return "index";
    } catch (Exception ex) {
      return today(model, page, sort);
    }
  }
}
