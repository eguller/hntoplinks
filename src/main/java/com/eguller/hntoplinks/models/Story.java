package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.util.DateUtils;

import java.time.LocalDateTime;

public record Story (
        Long id,
        long hnId,
        int commentCount,
        String title,
        String url,
        String domain,
        String by,
        int score,
        LocalDateTime createDate
){
    public static StoryEntity toStoryEntity(Story story){
        var storyEntity = new StoryEntity();
        storyEntity.setId(story.id());
        storyEntity.setHnid(story.hnId());
        storyEntity.setComment(story.commentCount());
        storyEntity.setPoints(story.score());
        storyEntity.setUrl(story.url());
        storyEntity.setComhead(story.domain());
        storyEntity.setLastUpdate(LocalDateTime.now());
        storyEntity.setUrl(story.url());
        storyEntity.setPoints(story.score());
        storyEntity.setUser(story.by());
        return storyEntity;
    }

    public static Story entityToStory(StoryEntity storyEntity){
        var story = new Story(
                storyEntity.getId(),
                storyEntity.getHnid(),
                storyEntity.getComment(),
                storyEntity.getTitle(),
                storyEntity.getUrl(),
                storyEntity.getComhead(),
                storyEntity.getUser(),
                storyEntity.getPoints(),
                storyEntity.getDate()
        );
        return story;
    }

    public String getSince(){
        return DateUtils.since(createDate);
    }
}
