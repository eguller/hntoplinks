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
    @Autowired
    private StoryRepository storyRepository;

    public void saveStories(List<Story> hnStoryList){
        hnStoryList.forEach(story -> {
            var itemOpt = storyRepository.findByHnid(story.hnId());
            itemOpt.or(() -> Optional.of(Story.toStoryEntity(story)))
                    .ifPresent(storyEntity -> {
                var newStoryEntity = Story.toStoryEntity(story);
                newStoryEntity.setId(storyEntity.getId());
                storyRepository.save(newStoryEntity);
            });
        });
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
