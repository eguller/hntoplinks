package com.eguller.hntoplinks.models;

import java.util.List;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.entities.SortType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StoriesContent {
  private List<Item> stories;
  private SortType sortBy;
  private int currentPage;
  private int totalPages;
}
