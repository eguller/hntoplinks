package com.eguller.hntoplinks.models;

import com.eguller.hntoplinks.entities.Item;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class StoriesContent {
  private List<Item> stories;
  private String     sortBy;
  private int currentPage;
  private int totalPages;
}
