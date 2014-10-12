package models;


import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
public class Item extends Model implements Cloneable{
	private static final long DAY = 1000 * 60 * 60 * 24;
	private static final long WEEK = 7 * DAY;
	private static final long MONTH = 30 * DAY;
	private static final long YEAR = DAY * 365;



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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name = "POSTURL")
	public String url;
    @Column(name = "COMHEAD")
	public String comhead;
    @Column(name = "HNID")
	public long hnid;
    @Column(name = "POINTS")
	public int points;
    @Column(name = "POSTDATE")
	public Date date;
    @Column(name = "HNUSER")
	public String user;
    @Column(name = "COMMENT")
	public int comment;
    @Column(name = "TITLE")
	public String title;
    @Column(name = "LASTUPDATE")
	public Date lastUpdate;
	
	public Item(String title, String url, String comhead, String user, Date date, long hnid, int points, int comment){
		if(url == null || date == null || hnid < 0 || points < 0){
			throw new IllegalArgumentException(
			new StringBuilder("url: ").append(url)
			.append(", title: ").append(title)
			.append(", comhead: ").append(comhead)
			.append(", user: ").append(user)
			.append(", date: ").append(date)
			.append(", hnid: ").append(hnid)
			.append(", points: ")
			.append(points).toString());
		}
		this.url = url;
		this.comhead = comhead;
		this.hnid = hnid;
		this.date = date;
		this.user = user;
		this.comment = comment;
		this.title = title;
		this.points = points;
		this.lastUpdate = Calendar.getInstance().getTime();
	}

    public Item(JSonItem jSonItem) {
        this(jSonItem.getTitle(),
            jSonItem.getUrl(),
            jSonItem.getDomainName(),
            jSonItem.getBy(),
            new Date(TimeUnit.SECONDS.toMillis(jSonItem.getTime())),
            jSonItem.getId(),
            jSonItem.getScore(),
            jSonItem.getKids().size()
            );
    }
	
	public static Item getByHnId(long hnid){
		return Item.find("byHnid", hnid).first();
	}
	
	public Item update(Item item){
		this.points = item.points;
		this.comhead = item.comhead;
		this.url = item.url;
		this.comment = item.comment;
        this.title = item.title;
		this.lastUpdate = Calendar.getInstance().getTime();
		return this;
	}
	
	public static List<Item> getAll(int page){
		return getAfter(new Date(0L), page);
	}
	
	public static List<Item> getDay(int page){
		return getAfter(new Date(Calendar.getInstance().getTimeInMillis() - DAY), page);
	}
	
	public static List<Item> getWeek(int page){
		return getAfter(new Date(Calendar.getInstance().getTimeInMillis() - WEEK), page);
	}
	
	public static List<Item> getMonth(int page){
		return getAfter(new Date(Calendar.getInstance().getTimeInMillis() - MONTH), page);
	}
	
	public static List<Item> getYear(int page){
		return getAfter(new Date(Calendar.getInstance().getTimeInMillis() - YEAR), page);
	}
	
	public static List<Item> getAfter(Date date, int page){
		return Item.find("date > ?1 order by points desc", date).fetch(page, 30);
	}

    public String getCommentStr(){
        if(comment > -1){
            if(comment > 1){
                return String.format("%d comments", comment);
            } else {
                return String.format("%d comment", comment);
            }
        } else {
            return "comments";
        }
    }

    public String getPointStr(){
        if(points > -1){
            if(points > 1){
                return String.format("%d points", points);
            } else {
                return String.format("%d point", points);
            }
        } else {
            return "0 point";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;

        return hnid == item.hnid;

    }

    @Override
    public int hashCode() {
        int result = 3;
        result = 31 * result + (int) (hnid ^ (hnid >>> 32));
        return result;
    }

    public Item clone(){
        return new Item(title, url, comhead, user, date, hnid, points, comment);
    }
}
