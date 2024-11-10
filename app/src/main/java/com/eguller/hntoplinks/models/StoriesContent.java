package com.eguller.hntoplinks.models;

import java.util.List;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.entities.SortType;

import com.eguller.hntoplinks.util.StoriesUtils;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StoriesContent {
  private List<Item> stories;
  private SortType sortBy;
  private int currentPage;
  private int    totalPages;
  private String title;

  @Builder.Default
  private int storiesPerPage = StoriesUtils.PAGE_SIZE;
}
