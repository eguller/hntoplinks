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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@ApplicationScope
public class StoryCacheService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int CACHE_SIZE = 300;
    private static final Comparator<Story> STORY_COMPARATOR = (story1, story2) -> story2.score() - story1.score();

    private SortedSet<Story> dailyCache = Collections.synchronizedSortedSet(new TreeSet<>(STORY_COMPARATOR));
    private SortedSet<Story> weeklyCache = Collections.synchronizedSortedSet(new TreeSet<>(STORY_COMPARATOR));
    private SortedSet<Story> monthlyCache = Collections.synchronizedSortedSet(new TreeSet<>(STORY_COMPARATOR));
    private SortedSet<Story> annuallyCache = Collections.synchronizedSortedSet(new TreeSet<>(STORY_COMPARATOR));
    private SortedSet<Story> allTimeCache = Collections.synchronizedSortedSet(new TreeSet<>(STORY_COMPARATOR));

    @Autowired
    private StoryService storyService;

    public List<Story> getDailyTop() {
        return new ArrayList<>(dailyCache);
    }

    public List<Story> getWeeklTop() {
        return new ArrayList<>(weeklyCache);
    }

    public List<Story> getMonthlyTop() {
        return new ArrayList<>(monthlyCache);
    }
    
    public List<Story> getAnnuallyTop() {
        return new ArrayList<>(annuallyCache);

    }

    public List<Story> getAllTimeTop() {
        return new ArrayList<>(allTimeCache);
    }

    public void addNewStories(List<Story> storyList) {
        updateCache(dailyCache, storyList);
        updateCache(weeklyCache, storyList);
        updateCache(monthlyCache, storyList);
        updateCache(annuallyCache, storyList);
        updateCache(allTimeCache, storyList);
    }

    @EventListener
    void loadStoriesOnStartup(ApplicationStartedEvent applicationStartedEvent) {
        var dailyTop = storyService.readDailyTop();
        var weeklyTop = storyService.readWeeklyTop();
        var monthlyTop = storyService.readMonthlyTop();
        var annuallyTop = storyService.readyAnnuallyTop();
        var allTimeTop = storyService.readAllTimeTop();
        logger.info("Stories are loaded from db. daily={}, weekly={}, monthly={}, annually={}, allTimeTop={}",
                dailyTop.size(), weeklyTop.size(), monthlyTop.size(), annuallyTop.size(), allTimeTop.size());
        updateCache(dailyCache, dailyTop);
        updateCache(weeklyCache, weeklyTop);
        updateCache(monthlyCache, monthlyTop);
        updateCache(annuallyCache, annuallyTop);
        updateCache(allTimeCache, allTimeTop);
    }

    private void updateCache(SortedSet<Story> originalList, List<Story> storyList) {
        originalList.addAll(storyList);
        while (originalList.size() > CACHE_SIZE) {
            originalList.remove(originalList.last());
        }
    }
}
