package models;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: eguller
 * Date: 10/12/14
 * Time: 4:31 PM
 */
public class JSonItem {
    private long id;
    private boolean deleted = false;
    private String type;
    private String by;
    private long time;
    private String text;
    private boolean dead;
    private long parent;
    private List<Long> kids = new ArrayList<Long>();
    private String url;
    private int score;
    private List<Long> parts = new ArrayList<Long>();
    private String title;

    public void setId(long id) {
        this.id = id;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public void setKids(List<Long> kids) {
        this.kids = kids;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setParts(List<Long> parts) {
        this.parts = parts;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getType() {
        return type;
    }

    public String getBy() {
        return by;
    }

    public long getTime() {
        return time;
    }

    public boolean isDead() {
        return dead;
    }

    public long getParent() {
        return parent;
    }

    public List<Long> getKids() {
        return kids;
    }

    public String getUrl() {
        return url;
    }

    public int getScore() {
        return score;
    }

    public List<Long> getParts() {
        return parts;
    }

    public String getDomainName(){
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if(domain != null) {
                return domain.startsWith("www.") ? domain.substring(4) : domain;
            } else {
                return extractDomainName();
            }
        } catch (URISyntaxException e) {
            return extractDomainName();
        }
    }

    private String extractDomainName() {
        String tmpUrl = url;
        if(tmpUrl.startsWith("http://")){
            tmpUrl = tmpUrl.substring("http://".length());
        }
        if(tmpUrl.startsWith("https://")){
            tmpUrl = tmpUrl.substring("https://".length());
        }
        if(tmpUrl.startsWith("www.")){
            tmpUrl = tmpUrl.substring("www.".length());
        }
        int urlPathSeperator = tmpUrl.indexOf("/");
        if(urlPathSeperator > -1){
            return tmpUrl.substring(0 , urlPathSeperator);
        } else {
            return tmpUrl;
        }
    }


}
