package com.eguller.hntoplinks.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import com.eguller.hntoplinks.entities.SortType;
import com.eguller.hntoplinks.models.Breadcrumb;
import com.eguller.hntoplinks.models.Navigation;
import com.eguller.hntoplinks.models.Page;
import com.eguller.hntoplinks.models.StoriesContent;
import com.eguller.hntoplinks.repository.ItemsRepository;
import com.eguller.hntoplinks.util.DateUtils;
import com.eguller.hntoplinks.util.SanitizedDate;
import com.eguller.hntoplinks.util.StoriesUtils;

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

  /** Support for legacy URLs */
  @GetMapping("/today/{page:[1-9][0-9]*}")
  public String todayLegacy(
      Model model,
      @PathVariable int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    return today(model, page, sort);
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
            .title("Daily Top Stories")
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

  /** Support for legacy URLs */
  @GetMapping("/week/{page:[1-9][0-9]*}")
  public String weekLegacy(
      Model model,
      @PathVariable int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    return week(model, page, sort);
  }

  @GetMapping("/week")
  public String week(
      Model model,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getIntervalForLastWeek();
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
        StoriesContent.builder()
            .title("Weekly Best")
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

  /** Support for legacy URLs */
  @GetMapping("/month/{page:[1-9][0-9]*}")
  public String monthLegacy(
      Model model,
      @PathVariable int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    return month(model, page, sort);
  }

  @GetMapping("/month")
  public String month(
      Model model,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getIntervalForLastMonth();
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
        StoriesContent.builder()
            .title("Monthly Highlights")
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

  /** Support for legacy URLs */
  @GetMapping("/year/{page:[1-9][0-9]*}")
  public String yearLegacy(
      Model model,
      @PathVariable int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    return year(model, page, sort);
  }

  @GetMapping("/year")
  public String year(
      Model model,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var interval = DateUtils.getIntervalForLastYear();
    var items = itemRepository.findByInterval(interval, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
        StoriesContent.builder()
            .title("Yearly Favorites")
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

  /** Support for legacy URLs */
  @GetMapping("/all/{page:[1-9][0-9]*}")
  public String allLegacy(
      Model model,
      @PathVariable int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    return all(model, page, sort);
  }

  @GetMapping("/all")
  public String all(
      Model model,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "sort", defaultValue = "upvotes") SortType sort) {
    var items = itemRepository.findAll(sort, StoriesUtils.PAGE_SIZE, page);
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
            .title("All-Time Best")
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
    var title = "%d Archive".formatted(year);
    var items = itemRepository.findByYear(year, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
        StoriesContent.builder()
            .title(title)
            .stories(items)
            .sortBy(sort)
            .currentPage(page)
            .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
            .build();

    var sanitizedDate = DateUtils.sanitizeYear(year);

    var activeMenu = year == LocalDate.now().getYear() ? "year" : null;
    var navigation =
        Navigation.builder()
            .activeMenu(activeMenu)
            .breadcrumbs(generateBreadcrumbs(sanitizedDate))
            .build();

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
    var title = "%s Archive".formatted(date.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
    var items = itemRepository.findByMonth(year, month, sort, StoriesUtils.PAGE_SIZE, page);
    var storiesContent =
        StoriesContent.builder()
            .title(title)
            .stories(items)
            .sortBy(sort)
            .currentPage(page)
            .totalPages(items.size() < StoriesUtils.PAGE_SIZE ? page : page + 1)
            .build();

    var sanitizedDate = DateUtils.sanitizeDate(year, month, null);
    var navigation = Navigation.builder().breadcrumbs(generateBreadcrumbs(sanitizedDate)).build();

    var pageModel =
        Page.builder()
            .currentPath("/stories/%d/%s".formatted(year, sanitizedDate.getPaddedMonth()))
            .selectedYear(year)
            .selectedMonth(month)
            .title(title)
            .navigation(navigation)
            .content(storiesContent)
            .build();
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
    var title =
        "%s Archive".formatted(date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, EEEE")));

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

    var sanitizedDate = DateUtils.sanitizeDate(year, month, day);
    var navigation = Navigation.builder().breadcrumbs(generateBreadcrumbs(sanitizedDate)).build();

    var pageModel =
        Page.builder()
            .currentPath(
                "/stories/%d/%s/%s"
                    .formatted(year, sanitizedDate.getPaddedMonth(), sanitizedDate.getPaddedDay()))
            .selectedYear(year)
            .selectedMonth(month)
            .title(title)
            .navigation(navigation)
            .content(storiesContent)
            .build();
    model.addAttribute("page", pageModel);
    return "index";
  }

  private List<Breadcrumb> generateBreadcrumbs(SanitizedDate sanitizedDate) {
    var list = new ArrayList<Breadcrumb>();
    if (sanitizedDate.getYear() != null
        && sanitizedDate.getMonth() != null
        && sanitizedDate.getDay() != null) {
      list.add(
          Breadcrumb.builder()
              .title(sanitizedDate.getPaddedDay())
              .url(
                  "/stories/%d/%s/%s"
                      .formatted(
                          sanitizedDate.getYear(),
                          sanitizedDate.getPaddedMonth(),
                          sanitizedDate.getPaddedDay()))
              .build());
    }
    if (sanitizedDate.getYear() != null && sanitizedDate.getMonth() != null) {

      list.add(
          Breadcrumb.builder()
              .title(DateUtils.getDisplayName(sanitizedDate.getMonth()))
              .url(
                  "/stories/%d/%s"
                      .formatted(sanitizedDate.getYear(), sanitizedDate.getPaddedMonth()))
              .build());
    }
    if (sanitizedDate.getYear() != null) {
      list.add(
          Breadcrumb.builder()
              .title(sanitizedDate.getYear().toString())
              .url("/stories/%d".formatted(sanitizedDate.getYear()))
              .build());
    }

    Collections.reverse(list);
    return list;
  }
}
