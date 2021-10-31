package com.eguller.hntoplinks.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("item")
public class StoryEntity implements HnEntity {
    @Id
    @Column("id")
    private Long id;
    @Column("posturl")
    private String url;
    @Column("comhead")
    private String comhead;

    @Column("hnid")
    private long hnid;
    @Column("points")
    private int points;
    @Column("postdate")
    private LocalDateTime date;
    @Column("hnuser")
    private String user;
    @Column("comment")
    private int comment;
    @Column("title")
    private String title;
    @Column("lastupdate")
    private LocalDateTime lastUpdate;

    public StoryEntity(){

    }

    public StoryEntity(StoryEntity source){
        this.id = source.getId();
        this.url = source.getUrl();
        this.comhead = source.getComhead();
        this.hnid = source.getHnid();
        this.points = source.getPoints();
        this.date = source.getDate();
        this.user = source.getUser();
        this.comment = source.getComment();
        this.title = source.getTitle();
        this.lastUpdate = source.getLastUpdate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComhead() {
        return comhead;
    }

    public void setComhead(String comhead) {
        this.comhead = comhead;
    }

    public long getHnid() {
        return hnid;
    }

    public void setHnid(long hnid) {
        this.hnid = hnid;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
