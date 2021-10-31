package com.eguller.hntoplinks.jobs;


import com.eguller.hntoplinks.services.FirebaseioService;
import com.eguller.hntoplinks.services.StoryCacheService;
import com.eguller.hntoplinks.services.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * User: eguller
 * Date: 10/12/14
 * Time: 3:55 PM
 */
@Component
public class ReadStoriesJob {
    @Autowired
    private FirebaseioService firebaseioService;

    @Autowired
    private StoryCacheService storyCacheService;

    @Autowired
    private StoryService storyService;

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void doJob() {
        var hnStoryList = firebaseioService.readTopStories();
        var storyList = hnStoryList.stream().map(hnStory -> hnStory.toStory()).collect(Collectors.toList());
        storyService.saveStories(storyList);
        storyCacheService.addNewStories(storyList);
    }
}
