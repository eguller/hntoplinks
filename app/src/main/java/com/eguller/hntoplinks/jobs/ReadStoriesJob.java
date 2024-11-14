package com.eguller.hntoplinks.jobs;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.repository.CheckPointRepository;
import com.eguller.hntoplinks.repository.ItemsRepository;
import com.eguller.hntoplinks.services.FirebaseioService;

/** User: eguller Date: 10/12/14 Time: 3:55 PM */
@Component
public class ReadStoriesJob {
  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final long READ_ITEMS_BATCH_SIZE = 100;

  private final FirebaseioService firebaseioService;

  private ItemsRepository itemRepository;

  private CheckPointRepository checkPointRepository;

  private TaskScheduler taskScheduler;

  @Value("${hntoplinks.read-all-stories.cron.enabled}")
  private boolean readAllStoriesCronEnabled;

  @Autowired
  public ReadStoriesJob(
      FirebaseioService firebaseioService,
      ItemsRepository itemsRepository,
      CheckPointRepository checkPointRepository,
      TaskScheduler taskScheduler) {
    this.firebaseioService = firebaseioService;
    this.itemRepository = itemsRepository;
    this.checkPointRepository = checkPointRepository;
    this.taskScheduler = taskScheduler;
  }

  @EventListener(ContextRefreshedEvent.class)
  public void init() {
    if (readAllStoriesCronEnabled) {
      readAllStories();
    }
  }

  @Scheduled(cron = "${hntoplinks.top-stories.cron}")
  public void doJob() {
    var topStories = firebaseioService.readTopStories().stream().collect(Collectors.toSet());
    itemRepository.batchSave(topStories);
    var bestStories = firebaseioService.readBestStories().stream().collect(Collectors.toSet());
    itemRepository.batchSave(topStories);
  }

  public void readAllStories() {
    var maxItem = firebaseioService.getMaxItem();
    var lastItem = checkPointRepository.getLastItem();
    if (maxItem - lastItem > READ_ITEMS_BATCH_SIZE) {

      var start = System.currentTimeMillis();
      var futureMap = new HashMap<Long, Future<Item>>();
      for (var i = lastItem; i <= maxItem; i++) {
        var future = firebaseioService.readItemAsync(i);
        futureMap.put(i, future);

        if (i - lastItem > READ_ITEMS_BATCH_SIZE) {
          break;
        }
      }

      var items =
          futureMap.entrySet().stream()
              .map(
                  entry -> {
                    try {
                      return entry.getValue().get();
                    } catch (Exception e) {
                      logger.error("Error reading story {}", entry.getKey(), e);
                      return null;
                    }
                  })
              .filter(item -> item != null)
              .collect(Collectors.toSet());
      var readItemsTook = System.currentTimeMillis() - start;
      start = System.currentTimeMillis();
      itemRepository.batchSave(items);
      var saveItemsTook = System.currentTimeMillis() - start;
      logger.info("Read items took {} ms, save items took {} ms", readItemsTook, saveItemsTook);
      var checkPoint = items.stream().map(Item::getId).sorted().reduce((first, second) -> second);

      checkPoint.ifPresentOrElse(
          c -> {
            checkPointRepository.saveStoriesCheckPoint(c);
            taskScheduler.schedule(() -> readAllStories(), Instant.now());
          },
          () ->
              taskScheduler.schedule(
                  () -> readAllStories(), Instant.now().plus(20, ChronoUnit.SECONDS)));
    } else {
      taskScheduler.schedule(() -> readAllStories(), Instant.now().plus(20, ChronoUnit.SECONDS));
    }
  }

  @Async
  Future<Long> readAndSaveStory(Long storyId) {
    var story = firebaseioService.readItem(storyId);
    if (story != null) {
      itemRepository.save(story);
    }
    return new AsyncResult<>(storyId);
  }

  @Async
  Future<Item> readStory(Long storyId) {
    var story = firebaseioService.readItem(storyId);
    return new AsyncResult<>(story);
  }

  @Scheduled(cron = "${hntoplinks.top-stories.cron}")
  public void readTopStories() {
    var topStories = firebaseioService.readTopStories();
    var bestStories = firebaseioService.readBestStories();
    var newStories = firebaseioService.readNewStoriesNew();
    Stream.of(topStories, bestStories, newStories)
        .flatMap(List::stream)
        .forEach(
            item -> {
              if (item != null) {
                itemRepository.save(item);
              }
            });
  }
}
