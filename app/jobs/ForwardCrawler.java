package jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Item;

import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;

public class ForwardCrawler extends Job {
	private static final long MINUTE = 60 * 1000;
	private static final long HOUR = 60 * MINUTE;
	private static final long DAY = 24 * HOUR;
	Pattern parentPattern = null;

	@Override
	public void doJob() {
		int moreLinkCount = 0;
		String url = "http://news.ycombinator.com";
		parentPattern = Pattern
				.compile("<a href=\"item\\?id=\\d+\">parent</a>");
		while (true) {
			try {
				String content = extractContent(url);
				List<String> postList = extractPosts(content);
				for (String post : postList) {
					try {
						Item item = extractItem(post);
						if (item != null) {
							Item existing = Item.getByHnId(item.hnid);
							if (existing != null) {
								item = existing.update(item);
							}
							if (!JPA.em().getTransaction().isActive()) {
								JPA.em().getTransaction().begin();
							}
							item.save();
							JPA.em().getTransaction().commit();
						}
					} catch (Exception e) {
						Logger.error(e, "Error while saving multiple post: %s",
								post);
					}
				}
				
				url = extractMoreLink(content);
				moreLinkCount ++;
				if(moreLinkCount > 5){
					moreLinkCount = 0;
					url = "http://news.ycombinator.com";
				}
				
				Logger.info("Last update : %t", Calendar.getInstance().getTime());
				
			} catch (Exception e) {
				Logger.error(e, "Exception in forward crawler.");
			} finally {
				try {
					Thread.sleep(((long) (MINUTE * Math.random())) * 20 + 10 * MINUTE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private List<String> extractPosts(String content) {
		List<String> postList = null;
		String mainTable = extractSubStr(content,
				"<table border=0 cellpadding=0 cellspacing=0>", "</table>");
		if(mainTable == null){
			return Collections.EMPTY_LIST;
		}
		postList = Arrays.asList(mainTable
				.split("<tr><td align=right valign=top class=\"title\">"));
		if (postList!= null && postList.size() > 0 && postList.get(0).length() == 0) {
			return postList.subList(1, postList.size());
		} else {
			return postList;
		}
	}

	private static String extractContent(String url) {
		StringBuilder sb = new StringBuilder();
		try {
			URL yc;
			yc = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			Logger.error(e, "IP banned by hn");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	private String extractMoreLink(String content) {
		if(content.contains("<a href=\"news2\">More</a>")){
			return "http://news.ycombinator.com/news2";
		}
		String subStr = extractSubStr(content, "href=\"/x?fnid=", "\"");
		if (subStr == null) {
			return "http://news.ycombinator.com";
		} else {
			return "http://news.ycombinator.com/x?fnid=" + subStr;
		}
	}

	public static Item extractItem(String post) {
		Item item = null;
		if (post != null && !isChild(post)) {
			String url = null;
			String title = null;
			int points = -1;
			String user = null;
			Date time = null;
			String comhead = null;
			long hnid = -1;
			int comment = -1;
			String titleHtml = extractSubStr(post, "<td class=\"title\">",
					"</td>");
			if ("[dead]".equals(titleHtml)) {
				throw new IllegalArgumentException("Dead post: " + post);
			}

			if (titleHtml == null) {
				Logger.error("Unexpected text: %s", post);
				throw new IllegalArgumentException("Unexpected text: " + post);
			}

			url = extractSubStr(titleHtml, "<a href=\"", "\"");
			if (!titleHtml.contains("comhead")) {
				url = url == null ? null : url.trim();
				url = "http://news.ycombinator.com/" + url;
			} else {
				comhead = extractSubStr(titleHtml, "<span class=\"comhead\">",
						"</span>");
				if (comhead != null) {
					comhead = comhead.trim();
					comhead = comhead.substring(1, comhead.length() - 1);
				}
			}
			title = extractSubStrRegex(titleHtml, "<a href=\".+?\">", "</a>");
			title = title == null ? null : title.trim();

			String subText = extractSubStr(post, "<td class=\"subtext\">",
					"</td>");

			if (subText != null) {
				String hnidStr = extractSubStr(subText, "\"item?id=", "\">");
				try {
					hnid = Long.parseLong(hnidStr);
				} catch (NumberFormatException e) {
					Logger.error("Parsing hnid failed", hnidStr);
				}

				String pointStr = null;
				try {
					pointStr = extractSubStrRegex(subText,
							"<span id=score_\\d+?>", " points*");
					points = Integer.parseInt(pointStr);
				} catch (NumberFormatException e) {
					Logger.error("Points format error: ", pointStr);
				}

				user = extractSubStr(subText, "user?id=", "\">");

				String commentStr = extractSubStrRegex(subText,
						"<a href=\"item\\?id=\\d+?\">", " comments</a>");

				try {
					if (commentStr != null) {
						comment = Integer.parseInt(commentStr);
					}
				} catch (NumberFormatException e) {
					Logger.error("Error while parsin comments: %s", commentStr);
				}

				String timeAgo = extractSubStr(subText, user + "</a> ", " ago");
				time = findDate(timeAgo);
			}
			if (title == null || url == null || user == null
					|| time == null || hnid == -1 || points == -1) {
				Logger.error("Something wrong titleHtml: %s subText: %s",
						titleHtml, subText);
				return null;
			}
			item = new Item(title, url, comhead, user, time, hnid, points,
					comment);
		}

		return item;
	}

	public static Date findDate(String timeAgo) {
		if (timeAgo == null) {
			return null;
		}
		long postDate = Calendar.getInstance().getTimeInMillis();
		int param = Integer.parseInt(timeAgo.split(" ")[0]);
		if (timeAgo.endsWith("minutes") || timeAgo.endsWith("minute")) {
			postDate -= param * MINUTE;
		} else if (timeAgo.endsWith("hours") || timeAgo.endsWith("hour")) {
			postDate -= param * HOUR;
		} else if (timeAgo.endsWith("days") || timeAgo.endsWith("day")) {
			postDate -= param * DAY;
		} else {
			Logger.error("Time unit does not exist timeAgo: %s", timeAgo);
		}
		return new Date(postDate);
	}

	private static String extractSubStr(String text, String begin, String end) {
		if (begin == null || end == null)
			return null;
		int beginIndex = text.indexOf(begin);
		if (beginIndex == -1)
			return null;
		beginIndex += begin.length();
		int endIndex = text.indexOf(end, beginIndex);
		if (endIndex == -1)
			return null;
		return text.substring(beginIndex, endIndex);
	}

	private static String extractSubStrRegex(String text, String begin, String end) {
		if (text == null || begin == null || end == null)
			return null;
		Pattern pattern = Pattern.compile(begin);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			int beginIndex = matcher.end();
			pattern = Pattern.compile(end);
			String subStr = text.substring(beginIndex);
			matcher = pattern.matcher(subStr);
			if (matcher.find()) {
				int endIndex = matcher.start();
				return subStr.substring(0, endIndex);
			}
		}
		return null;
	}

	public static boolean isChild(String post) {
		Pattern parentPattern2 = Pattern
				.compile("<a href=\"item\\?id=\\d+\">parent</a>");
		Matcher m = parentPattern2.matcher(post);
		return m.find();
	}

	public static void main(String[] args) {
		String fnid = extractSubStr(extractContent("http://news.ycombinator.com"), "href=\"/x?fnid=", "\"");
		System.out.println(fnid);
	}

}
