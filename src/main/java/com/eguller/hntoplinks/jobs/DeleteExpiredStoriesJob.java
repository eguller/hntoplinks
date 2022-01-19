package com.eguller.hntoplinks.jobs;

import com.eguller.hntoplinks.repository.StoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class DeleteExpiredStoriesJob {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private StoryRepository storyRepository;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void deleteExpiredStories() {

        var yesterday  = LocalDateTime.now().minusDays(1);
        var lastWeek = LocalDateTime.now().minusWeeks(1);
        var lastMonth = LocalDateTime.now().minusMonths(1);
        var lastYear = LocalDateTime.now().minusYears(1);
        int numberOfDeletedRecords = storyRepository.deleteExpiredStories(yesterday, lastWeek, lastMonth, lastYear);
        logger.info("Expired stories have been deleted. deletedStories={}", numberOfDeletedRecords);
    }
}
