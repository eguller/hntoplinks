package jobs;

import cache.ItemCache;
import models.Item;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import play.db.jpa.NoTransaction;
import play.jobs.Job;
import utils.FormatUtil;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForwardCrawler extends Job {
    private static final int GET_TIMEOUT = 10000;
    private static final long OK = 200;
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21";
    private static final long MINUTE = 60 * 1000;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    Pattern parentPattern = null;

    @Override @NoTransaction
    public void doJob() {
        int moreLinkCount = 0;
        String url = "https://news.ycombinator.com";
        parentPattern = Pattern
                .compile("<a href=\"item\\?id=\\d+\">parent</a>");
        while (true) {
            try {

                String content = extractContent(url);
                List<String> postList = extractPosts(content);
                List<Item> newItemList = new ArrayList<Item>();
                JPAPlugin.startTx(false);
                for (String post : postList) {
                    try {
                        Item item = extractItem(post);
                        if (item != null) {
                            Item existing = Item.getByHnId(item.hnid);
                            if (existing != null) {
                                existing.update(item);
                            }
                            if (!JPA.em().getTransaction().isActive()) {
                                JPA.em().getTransaction().begin();
                            }

                            if(existing == null){
                                item.save();
                                newItemList.add(item.clone());
                            }
                            else{
                                existing.save();
                                newItemList.add(existing.clone());
                            }
                            JPA.em().getTransaction().commit();

                        }
                    } catch (Exception e) {
                        Logger.error(e, "Error while saving multiple post: %s",
                                post);
                    }
                }
                JPAPlugin.closeTx(false);
                ItemCache.getInstance().updateCache(newItemList);

                url = extractMoreLink(content);
                moreLinkCount ++;
                if(moreLinkCount == 6){
                    url = "https://news.ycombinator.com";
                }
                else if(moreLinkCount == 7){
                    url = "https://news.ycombinator.com/best";
                    moreLinkCount = 0;
                }

                Logger.info("Last update : %s", Calendar.getInstance().getTime().toString());

            } catch (Exception e) {
                Logger.error(e, "Exception in forward crawler.");
            } finally {
                try {
                    Thread.sleep(((long) (MINUTE * Math.random())) * 10 + 5 * MINUTE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static List<String> extractPosts(String content) {
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

    public static String extractContent(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            connection.userAgent(USER_AGENT);
            connection.followRedirects(true);
            connection.timeout(GET_TIMEOUT);
            long start = System.currentTimeMillis();
            Connection.Response response = connection.execute();
            long diff = System.currentTimeMillis() - start;
            int responseCode = response.statusCode();
            if(response.statusCode() == OK) {
                String body = response.body();
                Logger.info("%s retrieved, content length %d, time %s", url, body.length(), FormatUtil.millis2Seconds(diff));
                return response.body();
            } else {
                Logger.error("%s returned %d", url, responseCode);
                return "";
            }
        } catch (IOException e) {
            Logger.error(e, "%s cannot be read.", url);
            return "";
        }
    }

    private String extractMoreLink(String content) {
        if(content.contains("<a href=\"news2\">More</a>")){
            return "https://news.ycombinator.com/news2";
        }
        String subStr = extractSubStr(content, "href=\"/x?fnid=", "\"");
        if (subStr == null) {
            return "https://news.ycombinator.com";
        } else {
            return "https://news.ycombinator.com/x?fnid=" + subStr;
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
                url = "https://news.ycombinator.com/" + url;
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
                    Logger.error("Error while parsing comments: %s", commentStr);
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

    public static String getMoreLink(Document doc){
        Element element = doc.select("a[href=news2]").last();

        if(element != null){
            return "https://news.ycombinator.com/" + element.attr("href");
        }
        element = doc.select("a[href^=/x?fnid]").last();
        if(element != null){
            return "https://news.ycombinator.com" + element.attr("href");
        }
        return null;
    }

    /**
     * Extract content with jsoup maybe later.
     * @param doc
     * @return
     */
    public static List<Item> extractItem(Document doc){
        List<Item> itemList = new ArrayList<Item>();
        Elements itemRows = doc.select("tr");
        Iterator iterator = itemRows.iterator();
        while(iterator.hasNext()){
            Element element = (Element) iterator.next();
            Element titleElement = element.select(".title a").first();
            if(titleElement == null){
                continue;
            }
            String titleStr = titleElement.text().trim();
            String urlStr = titleElement.attr("href").trim();

            Element comHeadElement = element.select(".comhead").first();
            if(comHeadElement == null){
                continue;
            }

            String comheadStr = comHeadElement.text().trim();

            Element pointsElement = element.select("span[id^=score_]").first();
            if(pointsElement == null){
                continue;
            }
            String pointsStr = pointsElement.text();
            if(pointsStr == null){
                continue;
            }
            String[] pointsArr = pointsStr.split(" ");
            if(pointsArr.length != 2){
                continue;
            }
            int points = -1;
            try{
                points = Integer.parseInt(pointsArr[0]);
            }
            catch (NumberFormatException e) {
            }

            if(points < 0){
                continue;
            }
            Element userElement = element.select("a[href^=user]").first();
            if(userElement == null){
                continue;
            }

            String user = userElement.text().trim();

            Element dateElement = element.select(".subtext").first();
        }

        return itemList;
    }
}
