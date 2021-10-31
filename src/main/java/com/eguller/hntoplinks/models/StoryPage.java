package com.eguller.hntoplinks.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class StoryPage extends Page{
    private PageTab activeTab;
    private int currentPage;
    private List<Story> storyList;
    private boolean hasMoreStories;
    private int storyPerPage;

    public boolean hasMoreStories(){
        return hasMoreStories;
    }
}
