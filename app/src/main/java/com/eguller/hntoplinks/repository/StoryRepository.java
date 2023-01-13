package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.StoryEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoryRepository {
  private final StoryRepositoryDelegate storyRepositoryDelegate;

  public StoryRepository(StoryRepositoryDelegate storyRepositoryDelegate) {
    this.storyRepositoryDelegate = storyRepositoryDelegate;
  }

  public void saveStories(List<StoryEntity> hnStoryList) {
    var storyIds = hnStoryList.stream().map(story -> story.getHnid()).collect(Collectors.toList());
    var existingStories = storyRepositoryDelegate.findByHnidIn(storyIds);
    var existingStoriesMap = existingStories.stream()
      .collect(Collectors.toMap(storyEntity -> storyEntity.getHnid(), storyEntity -> storyEntity));

    hnStoryList.stream()
      .map(storyEntity -> {
        var existingStory = existingStoriesMap.get(storyEntity.getHnid());
        if (existingStory != null) {
          storyEntity.setId(existingStory.getId());
        }
        return storyEntity;
      })
      .map(storyEntity -> storyRepositoryDelegate.save(storyEntity))
      .collect(Collectors.toList());
  }

  public List<StoryEntity> readDailyTop() {
    var yesterday = LocalDateTime.now().minusDays(1);
    var stories = storyRepositoryDelegate.findTop300ByDateAfterOrderByPointsDesc(yesterday);
    return stories;
  }

  public List<StoryEntity> readWeeklyTop() {
    var lastWeek = LocalDateTime.now().minusDays(7);
    var stories = storyRepositoryDelegate.findTop300ByDateAfterOrderByPointsDesc(lastWeek);
    return stories;
  }

  public List<StoryEntity> readMonthlyTop() {
    var lastMonth = LocalDateTime.now().minusMonths(1);
    var stories = storyRepositoryDelegate.findTop300ByDateAfterOrderByPointsDesc(lastMonth);
    return stories;
  }

  public List<StoryEntity> readyAnnuallyTop() {
    var lastYear = LocalDateTime.now().minusYears(1);
    var stories = storyRepositoryDelegate.findTop300ByDateAfterOrderByPointsDesc(lastYear);
    return stories;
  }

  public List<StoryEntity> readAllTimeTop() {
    var stories = storyRepositoryDelegate.findTop300ByOrderByPointsDesc();
    return stories;
  }
  
  public Optional<StoryEntity> findByHnid(long hnid) {
    return storyRepositoryDelegate.findByHnid(hnid);
  }
}
