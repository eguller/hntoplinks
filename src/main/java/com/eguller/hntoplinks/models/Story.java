package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.eguller.hntoplinks.util.DateUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public record Story(
        Long id,
        long hnId,
        int commentCount,
        String title,
        String url,
        String domain,
        String by,
        int score,
        LocalDateTime createDate
) {
    public static StoryEntity toStoryEntity(Story story) {
        var storyEntity = new StoryEntity();
        storyEntity.setId(story.id());
        storyEntity.setHnid(story.hnId());
        storyEntity.setComment(story.commentCount());
        storyEntity.setDate(story.createDate());
        storyEntity.setPoints(story.score());
        storyEntity.setUrl(story.url());
        storyEntity.setComhead(story.domain());
        storyEntity.setLastUpdate(LocalDateTime.now());
        storyEntity.setUrl(story.url());
        storyEntity.setPoints(story.score());
        storyEntity.setUser(story.by());
        return storyEntity;
    }

    public static Story entityToStory(StoryEntity storyEntity) {
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

    public String getSince() {
        return DateUtils.since(createDate);
    }

    public boolean hasDomain(){
        return StringUtils.hasText(domain);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Story story = (Story) o;
        return hnId == story.hnId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hnId);
    }
}
