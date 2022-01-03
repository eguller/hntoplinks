package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.models.Story;
import com.eguller.hntoplinks.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public void saveStories(List<Story> hnStoryList){
        var storyIds = hnStoryList.stream().map(story -> story.hnId()) .collect(Collectors.toList());
        var existingStories = storyRepository.findByHnidIn(storyIds);
        var existingStoriesMap = existingStories.stream()
                .collect(Collectors.toMap(storyEntity -> storyEntity.getHnid(), storyEntity -> storyEntity));

        hnStoryList.stream()
                .map(story -> Story.toStoryEntity(story))
                .map(storyEntity -> {
                    var existingStory =  existingStoriesMap.get(storyEntity.getHnid());
                    if(existingStory != null){
                        storyEntity.setId(existingStory.getId());
                    }
                    return storyEntity;
                })
                .map(storyEntity -> storyRepository.save(storyEntity))
                .collect(Collectors.toList());
    }

    public List<Story> readDailyTop(){
        var yesterday = LocalDateTime.now().minusDays(1);
        var storyList = storyEntityListToStoryList(
                () -> storyRepository.findTop300ByDateAfterOrderByPointsDesc(yesterday)
        );
        return storyList;
    }

    public List<Story> readWeeklyTop(){
        var lastWeek = LocalDateTime.now().minusDays(7);
        var storyList = storyEntityListToStoryList(
                () -> storyRepository.findTop300ByDateAfterOrderByPointsDesc(lastWeek)
        );
        return storyList;
    }

    public List<Story> readMonthlyTop(){
        var lastMonth = LocalDateTime.now().minusMonths(1);
        var storyList = storyEntityListToStoryList(
                () -> storyRepository.findTop300ByDateAfterOrderByPointsDesc(lastMonth)
        );
        return storyList;
    }

    public List<Story> readyAnnuallyTop(){
        var lastYear = LocalDateTime.now().minusYears(1);
        var storyList = storyEntityListToStoryList(
                () -> storyRepository.findTop300ByDateAfterOrderByPointsDesc(lastYear)
        );
        return storyList;
    }

    public List<Story> readAllTimeTop(){
        var storyList = storyEntityListToStoryList(
                () -> storyRepository.findTop300ByOrderByPointsDesc()
        );
        return storyList;
    }

    private List<Story> storyEntityListToStoryList(Supplier<List<StoryEntity>> supplier){
        var storyList = supplier.get().
                stream()
                .map(storyEntity -> Story.entityToStory(storyEntity))
                .collect(Collectors.toList());
        return storyList;
    }
}
