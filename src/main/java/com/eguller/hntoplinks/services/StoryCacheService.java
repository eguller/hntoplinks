package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@ApplicationScope
public class StoryCacheService {

    private static final int CACHE_SIZE = 300;
    private List<Story> dailyCache = new ArrayList<>();
    private List<Story> weeklyCache = new ArrayList<>();
    private List<Story> monthlyCache = new ArrayList<>();
    private List<Story> annuallyCache = new ArrayList<>();
    private List<Story> allTimeCache = new ArrayList<>();

    @Autowired
    private StoryService storyService;

    public void updateDailyCache(List<Story> dailyCache){
        this.dailyCache = dailyCache;
    }

    public void updateWeeklyCache(List<Story> weeklyCache){
        this.weeklyCache = weeklyCache;
    }

    public void updateMonthlyCache(List<Story> monthlyCache){
        this.monthlyCache = monthlyCache;
    }

    public void updateAnnuallyCache(List<Story> annuallyCache){
        this.annuallyCache = annuallyCache;
    }

    public void setAllTimeCache(List<Story> allTimeCache){
        this.allTimeCache = allTimeCache;
    }

    public List<Story> getDailyTop(){
        return new ArrayList<>(dailyCache);
    }

    public List<Story> getWeeklTop(){
        return new ArrayList<>(weeklyCache);
    }

    public void addNewStories(List<Story> storyList) {
        dailyCache = createNewList(dailyCache, storyList);
        weeklyCache = createNewList(weeklyCache, storyList);
        monthlyCache = createNewList(monthlyCache, storyList);
        annuallyCache = createNewList(annuallyCache, storyList);
        allTimeCache = createNewList(allTimeCache, storyList);
    }

    @EventListener
    void loadStoriesOnStartup(ApplicationStartedEvent applicationStartedEvent){
        var dailyTop = storyService.readDailyTop();
        var weeklyTop = storyService.readWeeklyTop();
        var monthlyTop =  storyService.readMonthlyTop();
        var annuallyTop = storyService.readyAnnuallyTop();
        var allTimeTop = storyService.readAllTimeTop();
        dailyCache = createNewList(dailyCache, dailyTop);
        weeklyCache = createNewList(weeklyCache, weeklyTop);
        monthlyCache = createNewList(monthlyCache, monthlyTop);
        annuallyCache  = createNewList(annuallyCache, annuallyTop);
        allTimeCache = createNewList(allTimeCache, allTimeTop);
    }

    private List<Story> createNewList(List<Story> originalList, List<Story> storyList){
        List<Story> tmpList = new ArrayList<>(originalList);
        tmpList.addAll(storyList);
        tmpList.sort(Comparator.comparingInt(Story::score));
        return tmpList.subList(0, Math.min(CACHE_SIZE, Math.max(0, tmpList.size() - 1)));
    }
}
