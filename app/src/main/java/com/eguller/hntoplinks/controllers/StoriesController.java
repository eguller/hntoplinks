package com.eguller.hntoplinks.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.eguller.hntoplinks.util.StoriesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import com.eguller.hntoplinks.entities.SortType;
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
    @RequestParam(value = "day", required = false) Integer day,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    if (year != null && month != null && day != null) {
      return byDay(model, year, month, day, page, sort);
    } else if (year != null & month != null) {
      return byMonth(model, year, month, page, sort);
    } else if (year != null) {
      return byYear(model, year, page, sort);
    } else {
      return today(model, page, sort);
    }
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
        .title("Stories of %s".formatted(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))))
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
        .title("Stories of %d".formatted(LocalDate.now().getYear()))
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
        .title("All Time")
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

  @GetMapping("/stories/{year:\\d{4,}}")
  public String byYear(
    Model model,
    @PathVariable int year,
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "sort", required = false, defaultValue = "upvotes") SortType sort) {
    var title = "Stories from %d".formatted(year);
    var interval = DateUtils.getInterval(year);
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .title(title)
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var activeMenu = year == LocalDate.now().getYear() ? "year" :  null;
    var navigation = Navigation.builder().activeMenu(activeMenu).build();

    var pageModel =
      Page.builder()
        .currentPath("/stories/%d".formatted(year))
        .selectedYear(year)
        .title(title)
        .navigation(navigation)
        .content(storiesContent)
        .build();

    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/stories/{year:\\d{4,}}/{month:\\d{1,2}}")
  public String byMonth(
    Model model,
    @PathVariable int year,
    @PathVariable int month,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var date = LocalDate.of(year, month, 1);
    var title = "Stories from %s".formatted(date.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
    var interval = DateUtils.getInterval(year, month);
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .title(title)
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().build();

    var pageModel =
      Page.builder()
        .currentPath("/stories/%d/%d".formatted(year, month))
        .selectedYear(year)
        .selectedMonth(month)
        .title(title)
        .navigation(navigation)
        .content(storiesContent).build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  @GetMapping("/stories/{year:\\d{4,}}/{month:\\d{1,2}}/{day:\\d{1,2}}")
  public String byDay(
    Model model,
    @PathVariable("year") int year,
    @PathVariable("month") int month,
    @PathVariable("day") int day,
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var date = LocalDate.of(year, month, 1);
    var title = "Stories from %s".formatted(date.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

    var interval = DateUtils.getInterval(year, month, day);

    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
      StoriesContent.builder()
        .title(title)
        .stories(items)
        .sortBy(sort)
        .currentPage(page)
        .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
        .build();

    var navigation = Navigation.builder().build();

    var pageModel =
      Page
        .builder()
        .currentPath("/stories/%d/%d/%d".formatted(year, month, day))
        .selectedYear(year)
        .selectedMonth(month)
        .title(title)
        .navigation(navigation)
        .content(storiesContent)
        .build();
    model.addAttribute("page", pageModel);
    return "index";
  }
}
