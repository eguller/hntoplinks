package com.eguller.hntoplinks.models;

import java.util.List;

public class StoryPage {
    private Page page;
    private PageTab activeTab;
    private int currentPage;
    private List<Story> storyList;
    private boolean hasMoreStories;
    private int storyPerPage;

    StoryPage(Page page, PageTab activeTab, int currentPage, List<Story> storyList, boolean hasMoreStories, int storyPerPage) {
        this.page = page;
        this.activeTab = activeTab;
        this.currentPage = currentPage;
        this.storyList = storyList;
        this.hasMoreStories = hasMoreStories;
        this.storyPerPage = storyPerPage;
    }

    public static StoryPageBuilder builder() {
        return new StoryPageBuilder();
    }

    public boolean hasMoreStories(){
        return hasMoreStories;
    }

    public Page getPage() {
        return this.page;
    }

    public PageTab getActiveTab() {
        return this.activeTab;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public List<Story> getStoryList() {
        return this.storyList;
    }

    public boolean isHasMoreStories() {
        return this.hasMoreStories;
    }

    public int getStoryPerPage() {
        return this.storyPerPage;
    }

    public static class StoryPageBuilder {
        private Page page;
        private PageTab activeTab;
        private int currentPage;
        private List<Story> storyList;
        private boolean hasMoreStories;
        private int storyPerPage;

        StoryPageBuilder() {
        }

        public StoryPageBuilder page(Page page) {
            this.page = page;
            return this;
        }

        public StoryPageBuilder activeTab(PageTab activeTab) {
            this.activeTab = activeTab;
            return this;
        }

        public StoryPageBuilder currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public StoryPageBuilder storyList(List<Story> storyList) {
            this.storyList = storyList;
            return this;
        }

        public StoryPageBuilder hasMoreStories(boolean hasMoreStories) {
            this.hasMoreStories = hasMoreStories;
            return this;
        }

        public StoryPageBuilder storyPerPage(int storyPerPage) {
            this.storyPerPage = storyPerPage;
            return this;
        }

        public StoryPage build() {
            return new StoryPage(page, activeTab, currentPage, storyList, hasMoreStories, storyPerPage);
        }

        public String toString() {
            return "StoryPage.StoryPageBuilder(page=" + this.page + ", activeTab=" + this.activeTab + ", currentPage=" + this.currentPage + ", storyList=" + this.storyList + ", hasMoreStories=" + this.hasMoreStories + ", storyPerPage=" + this.storyPerPage + ")";
        }
    }
}
