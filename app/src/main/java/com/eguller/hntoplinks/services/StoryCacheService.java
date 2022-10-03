package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@ApplicationScope
public class StoryCacheService {
  private static final Logger logger     = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final int    CACHE_SIZE = 300;

  private Cache dailyCache    = new Cache(CACHE_SIZE, (story) -> story.createDate().isAfter(LocalDateTime.now().minusDays(1)));
  private Cache weeklyCache   = new Cache(CACHE_SIZE, (story) -> story.createDate().isAfter(LocalDateTime.now().minusWeeks(1)));
  private Cache monthlyCache  = new Cache(CACHE_SIZE, (story) -> story.createDate().isAfter(LocalDateTime.now().minusMonths(1)));
  private Cache annuallyCache = new Cache(CACHE_SIZE, (story) -> story.createDate().isAfter(LocalDateTime.now().minusYears(1)));
  private Cache allTimeCache  = new Cache(CACHE_SIZE, (story) -> true);

  @Autowired
  private StoryService storyService;

  public List<Story> getDailyTop() {
    return dailyCache.getCache();
  }

  public List<Story> getWeeklTop() {
    return weeklyCache.getCache();
  }

  public List<Story> getMonthlyTop() {
    return monthlyCache.getCache();
  }

  public List<Story> getAnnuallyTop() {
    return annuallyCache.getCache();
  }

  public List<Story> getAllTimeTop() {
    return allTimeCache.getCache();
  }

  public synchronized void addNewStories(List<Story> storyList) {
    dailyCache.updateCache(storyList);
    weeklyCache.updateCache(storyList);
    monthlyCache.updateCache(storyList);
    annuallyCache.updateCache(storyList);
    allTimeCache.updateCache(storyList);
  }

  @EventListener
  synchronized void loadStoriesOnStartup(ApplicationStartedEvent applicationStartedEvent) {
    var dailyTop = storyService.readDailyTop();
    var weeklyTop = storyService.readWeeklyTop();
    var monthlyTop = storyService.readMonthlyTop();
    var annuallyTop = storyService.readyAnnuallyTop();
    var allTimeTop = storyService.readAllTimeTop();
    logger.info("Stories are loaded from db. daily={}, weekly={}, monthly={}, annually={}, allTimeTop={}",
      dailyTop.size(), weeklyTop.size(), monthlyTop.size(), annuallyTop.size(), allTimeTop.size());
    dailyCache.updateCache(dailyTop);
    weeklyCache.updateCache(weeklyTop);
    monthlyCache.updateCache(monthlyTop);
    annuallyCache.updateCache(annuallyTop);
    allTimeCache.updateCache(allTimeTop);
  }

  private static class Cache {

    private static final Comparator<Story> STORY_COMPARATOR = (story1, story2) -> story2.score() - story1.score();
    private final        int               cacheSize;
    private final        Predicate<Story>  newStoryFilter;
    private              List<Story>       cache;

    public Cache(int cacheSize, Predicate<Story> newStoryFilter) {
      this.cacheSize      = cacheSize;
      this.newStoryFilter = newStoryFilter;
      this.cache          = new ArrayList<>();
    }

    public List<Story> getCache() {
      return Collections.unmodifiableList(cache);
    }

    public void updateCache(List<Story> newStories) {
      var existingCache = new ArrayList<>(cache);
      var storyMap = existingCache.stream().collect(Collectors.toMap(story -> story.hnId(), story -> story));
      newStories.stream().filter(newStoryFilter).forEach(story -> storyMap.put(story.hnId(), story));
      var updatedStories = storyMap.values().stream().toList();
      var sortedStories = updatedStories.stream().sorted(STORY_COMPARATOR).toList();
      this.cache = sortedStories.subList(0, Math.min(updatedStories.size(), cacheSize));
    }

  }
}
