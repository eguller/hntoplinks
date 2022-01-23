package com.eguller.hntoplinks.jobs;


import com.eguller.hntoplinks.models.HnStory;
import com.eguller.hntoplinks.services.FirebaseioService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.StoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * User: eguller
 * Date: 10/12/14
 * Time: 3:55 PM
 */
@Component
public class ReadStoriesJob {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FirebaseioService firebaseioService;

    private StoryCacheService storyCacheService;

    private StoryService storyService;

    @Autowired
    public ReadStoriesJob(FirebaseioService firebaseioService, StoryCacheService storyCacheService, StoryService storyService) {
        this.firebaseioService = firebaseioService;
        this.storyCacheService = storyCacheService;
        this.storyService = storyService;
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void doJob() {
        var topStories = firebaseioService.readTopStories();
        saveStories(topStories);
        var bestStories = firebaseioService.readBestStories();
        saveStories(bestStories);
    }

    private void saveStories(List<HnStory> hnStoryList) {
        logger.info("Stories read from firebase. numberOfStories={}", hnStoryList.size());
        var storyList = hnStoryList.stream().map(hnStory -> hnStory.toStory()).collect(Collectors.toList());
        storyService.saveStories(storyList);
        storyCacheService.addNewStories(storyList);
    }
}
