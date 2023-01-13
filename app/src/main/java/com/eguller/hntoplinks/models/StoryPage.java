package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.StoryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class StoryPage extends Page {
  private PageTab           activeTab;
  private int               currentPage;
  private List<StoryEntity> storyList;
  private boolean           hasMoreStories;
  private int               storyPerPage;
}
