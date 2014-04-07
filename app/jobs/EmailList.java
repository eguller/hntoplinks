package jobs;

import models.Item;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.Play;
import play.libs.Mail;
import play.vfs.VirtualFile;
import utils.EmailUtil;
import utils.Templater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: eguller
 * Date: 4/6/14
 * Time: 11:23 PM
 */
public abstract class EmailList {

    public abstract void send();

    public abstract String subject();

    public abstract void sendEmail(List<Subscription> subscriptions, List<Item> itemList, String subject) throws EmailException;

    protected void sendEmail(Subscription subscription, List<Item> itemList, String subjectTime) throws EmailException {
        String htmlContent = createHtml(subscription.getSubsUUID(), itemList, subjectTime);
        String textContent = createText(subscription.getSubsUUID(), itemList, subjectTime);
        EmailUtil.sendEmail(htmlContent, textContent, subscription.getEmail(), subjectTime + " - Hacker News Top Links");

    }


    private String createHtml(String subscriptionId, List<Item> itemList, String subject) {
        String itemContent = templateItems(itemList);
        Map<String, String> values = new HashMap<String, String>();
        values.put("items", itemContent);
        values.put("subject", subject);
        values.put("subscriptionid", subscriptionId);
        String content = VirtualFile.fromRelativePath("/app/template/email.html").contentAsString();
        String templated = Templater.template(content, values);
        return templated;
    }

    private String templateItems(List<Item> itemList) {
        String content = VirtualFile.fromRelativePath("/app/template/item.html").contentAsString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            Map<String, String> values = new HashMap<String, String>();
            values.put("url", item.getUrl());
            values.put("index", String.valueOf(i + 1));
            values.put("title", item.getTitle());
            values.put("comhead", item.getComhead());
            values.put("userid", item.getUser());
            values.put("username", item.getUser());
            values.put("points", String.valueOf(item.getPoints()));
            values.put("comment", String.valueOf(item.getComment()));
            values.put("hnid", String.valueOf(item.getHnid()));
            values.put("applicationBaseUrl", Play.configuration.getProperty("application.baseUrl"));
            String templated = Templater.template(content, values);
            sb.append(templated);
        }
        return sb.toString();
    }


    private String createText(String subscriptionId, List<Item> itemList, String subject) {
        StringBuilder sb = new StringBuilder();
        sb.append(subject);
        sb.append("\n");
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            sb.append(i + 1);
            sb.append(". ");
            sb.append(item.getTitle());
            sb.append(" (");
            sb.append(item.getPoints());
            sb.append(" points) ");
            sb.append(item.getUrl());
            sb.append("\n");
        }
        sb.append("\n");
        sb.append("Unsubscribe - ").append(Play.configuration.getProperty("application.baseUrl")).append("/unsubscribe/").append(subscriptionId).append("\n");
        sb.append("Modify Subscription - ").append(Play.configuration.getProperty("application.baseUrl")).append("/subscription/modify/").append(subscriptionId).append("\n");
        return sb.toString();
    }
}
