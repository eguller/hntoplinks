package com.eguller.hntoplinks.models;

public class Page {
    private String title;

    Page(String title) {
        this.title = title;
    }

    public static PageBuilder builder() {
        return new PageBuilder();
    }

    public String getTitle() {
        return this.title;
    }

    public static class PageBuilder {
        private String title;

        PageBuilder() {
        }

        public PageBuilder title(String title) {
            this.title = title;
            return this;
        }

        public Page build() {
            return new Page(title);
        }

        public String toString() {
            return "Page.PageBuilder(title=" + this.title + ")";
        }
    }
}

