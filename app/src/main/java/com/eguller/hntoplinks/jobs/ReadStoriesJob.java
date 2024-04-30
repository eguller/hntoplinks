package com.eguller.hntoplinks.jobs;


import com.eguller.hntoplinks.models.HnStory;
import com.eguller.hntoplinks.repository.CheckPointRepository;
import com.eguller.hntoplinks.repository.ItemRepository;
import com.eguller.hntoplinks.repository.StoryRepository;
import com.eguller.hntoplinks.services.FirebaseioService;
import com.eguller.hntoplinks.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: eguller
 * Date: 10/12/14
 * Time: 3:55 PM
 */
@Component
public class ReadStoriesJob {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final long READ_ITEMS_BATCH_SIZE = 10;

  private final FirebaseioService firebaseioService;

  private StoryRepository storyRepository;

  private StatisticsService statisticsService;


  private ItemRepository itemRepository;

  private CheckPointRepository checkPointRepository;

  private TaskScheduler taskScheduler;

  @Value("${hntoplinks.read-all-stories.cron.enabled}")
  private boolean readAllStoriesCronEnabled;

  @Autowired
  public ReadStoriesJob(FirebaseioService firebaseioService,
                        StoryRepository storyRepository,
                        StatisticsService statisticsService,
                        ItemRepository itemsRepository,
                        CheckPointRepository checkPointRepository,
                        TaskScheduler taskScheduler
  ) {
    this.firebaseioService    = firebaseioService;
    this.storyRepository      = storyRepository;
    this.statisticsService    = statisticsService;
    this.itemRepository       = itemsRepository;
    this.checkPointRepository = checkPointRepository;
    this.taskScheduler        = taskScheduler;
  }

  @EventListener(ContextRefreshedEvent.class)
  public void init() {
    if (readAllStoriesCronEnabled) {
      readAllStories();
    }
  }

    @Scheduled(cron = "${hntoplinks.top-stories.cron}")
    public void doJob () {
      var topStories = firebaseioService.readTopStories();
      saveStories(topStories);
      var bestStories = firebaseioService.readBestStories();
      saveStories(bestStories);
      //statisticsService.updateLastHnUpdate();
    }

    private void saveStories (List < HnStory > hnStoryList) {
      logger.info("Stories read from firebase. numberOfStories={}", hnStoryList.size());
      var storyList = hnStoryList.stream().map(hnStory -> hnStory.toStory()).collect(Collectors.toList());
      storyRepository.saveStories(storyList);
    }

    //@Scheduled(fixedDelay = 10, initialDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void readAllStories () {
      var maxItem = firebaseioService.getMaxItem();
      var lastItem = checkPointRepository.getLastItem();
      if (maxItem - lastItem > READ_ITEMS_BATCH_SIZE) {
        for (var i = lastItem; i <= maxItem; i++) {
          var item = firebaseioService.readItem(i);
          if (item != null) {
            itemRepository.save(item);
          }
          checkPointRepository.saveStoriesCheckPoint(i);
          if (i - lastItem > READ_ITEMS_BATCH_SIZE) {
            break;
          }
          taskScheduler.schedule(() -> readAllStories(), Instant.now());
        }
      } else {
        taskScheduler.schedule(() -> readAllStories(), Instant.now().plus(20, ChronoUnit.SECONDS));
      }
    }

    @Scheduled(cron = "${hntoplinks.top-stories.cron}")
    public void readTopStories () {
      var topStories = firebaseioService.readTopStoriesNew();
      var bestStories = firebaseioService.readBestStoriesNew();
      var newStories = firebaseioService.readNewStoriesNew();
      Stream.of(topStories, bestStories, newStories).flatMap(List::stream).forEach(item -> {
          if (item != null) {
            itemRepository.save(item);
          }
        }
      );
    }

  }
