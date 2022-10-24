package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.StoryEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Repository
public abstract class StoryRepository implements CrudRepository<StoryEntity, Long> {
  public void saveStories(List<StoryEntity> hnStoryList) {
    var storyIds = hnStoryList.stream().map(story -> story.getHnid()).collect(Collectors.toList());
    var existingStories = findByHnidIn(storyIds);
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
      .map(storyEntity -> save(storyEntity))
      .collect(Collectors.toList());
  }

  public List<StoryEntity> readDailyTop() {
    var yesterday = LocalDateTime.now().minusDays(1);
    var stories = this.findTop300ByDateAfterOrderByPointsDesc(yesterday);
    return stories;
  }

  public List<StoryEntity> readWeeklyTop() {
    var lastWeek = LocalDateTime.now().minusDays(7);
    var stories = this.findTop300ByDateAfterOrderByPointsDesc(lastWeek);
    return stories;
  }

  public List<StoryEntity> readMonthlyTop() {
    var lastMonth = LocalDateTime.now().minusMonths(1);
    var stories = this.findTop300ByDateAfterOrderByPointsDesc(lastMonth);
    return stories;
  }

  public List<StoryEntity> readyAnnuallyTop() {
    var lastYear = LocalDateTime.now().minusYears(1);
    var stories = this.findTop300ByDateAfterOrderByPointsDesc(lastYear);
    return stories;
  }

  public List<StoryEntity> readAllTimeTop() {
    var stories = this.findTop300ByOrderByPointsDesc();
    return stories;
  }

  public abstract Optional<StoryEntity> findByHnid(long l);

  public abstract List<StoryEntity> findTop300ByDateAfterOrderByPointsDesc(LocalDateTime date);

  public abstract List<StoryEntity> findTop300ByOrderByPointsDesc();

  public abstract List<StoryEntity> findByHnidIn(List<Long> hnIdList);

  @Modifying
  @Query("""
     delete
     from item
     where item.id not in (
         (select id from item where item.postdate > :yesterday order by points desc limit 500)
         union all
         (select id from item where item.postdate > :lastWeek order by points desc limit 500)
         union all
         (select id from item where item.postdate > :lastMonth order by points desc limit 500)
         union all
         (select id from item where item.postdate > :lastYear order by points desc limit 500)
         union all
         (select id from item order by points desc limit 500)
     )
    """)
  public abstract int deleteExpiredStories(LocalDateTime yesterday, LocalDateTime lastWeek, LocalDateTime lastMonth, LocalDateTime lastYear);

}
