package models;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import play.db.jpa.Model;

@Entity
public class Item extends Model{
	private static final long DAY = 1000 * 60 * 60 * 24;
	private static final long WEEK = 7 * DAY;
	private static final long MONTH = 30 * DAY;
	private static final long YEAR = DAY * 365;
	
	
	public String url;
	public String comhead;
	public long hnid;
	public int points;
	public Date date;
	public String user;
	public int comment;
	public String title;
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
}
