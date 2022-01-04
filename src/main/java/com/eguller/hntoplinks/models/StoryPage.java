package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StoryPage {
    private Page page;
    private PageTab activeTab;
    private int currentPage;
    private List<Story> storyList;
    private boolean hasMoreStories;
    private int storyPerPage;
}
