package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.StoryEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HnStory {
  private static final Logger     logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final        String     by;
  private final        long       id;
  private final        int        score;
  private final        long       time;
  private final        String     title;
  private final        String     url;
  private final        List<Long> kids;

  @JsonCreator
  public HnStory(
    @JsonProperty("by")
    String by,
    @JsonProperty("id")
    long id,
    @JsonProperty("score")
    int score,
    @JsonProperty("time")
    long time,
    @JsonProperty("title")
    String title,
    @JsonProperty("url")
    String url,
    @JsonProperty("kids")
    List<Long> kids) {
    this.by    = by;
    this.id    = id;
    this.score = score;
    this.time  = time;
    this.title = title;
    this.url   = url;
    this.kids  = kids == null ? Collections.emptyList() : kids;
  }

  public String getBy() {
    return by;
  }

  public long getId() {
    return id;
  }

  public int getScore() {
    return score;
  }

  public long getTime() {
    return time;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getInternalUrl() {
    return "https://news.ycombinator.com/item?id=" + getId();
  }

  public List<Long> getKids() {
    return kids;
  }

  public String getDomainName() {
    try {
      if (url == null) {
        if (title != null &&
          !(
            title.startsWith("Ask HN: ") ||
              title.startsWith("Show HN: ") ||
              title.startsWith("Help HN: ") ||
              title.startsWith("Poll: ")
          )
        ) {
          logger.warn("Url is null {}", id);
        }
        return "";
      }
      URI uri = new URI(url);
      String domain = uri.getHost();
      if (domain != null) {
        return domain.startsWith("www.") ? domain.substring(4) : domain;
      } else {
        return extractDomainName();
      }
    } catch (URISyntaxException e) {
      return extractDomainName();
    }
  }

  private String extractDomainName() {
    String tmpUrl = url;
    if (tmpUrl.startsWith("http://")) {
      tmpUrl = tmpUrl.substring("http://".length());
    }
    if (tmpUrl.startsWith("https://")) {
      tmpUrl = tmpUrl.substring("https://".length());
    }
    if (tmpUrl.startsWith("www.")) {
      tmpUrl = tmpUrl.substring("www.".length());
    }
    int urlPathSeperator = tmpUrl.indexOf("/");
    if (urlPathSeperator > -1) {
      return tmpUrl.substring(0, urlPathSeperator);
    } else {
      return tmpUrl;
    }
  }

  public StoryEntity toStory() {
    StoryEntity story = new StoryEntity();
    story.setHnid(this.getId());
    story.setComment(this.getKids().size());
    story.setTitle(this.getTitle());
    story.setUrl(this.getUrl());
    story.setComhead(this.getDomainName());
    story.setUser(this.getBy());
    story.setPoints(this.getScore());
    story.setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(this.getTime())), ZoneId.systemDefault())
    );
    story.setLastUpdate(story.getDate());
    return story;
  }

}
