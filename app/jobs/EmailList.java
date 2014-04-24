package jobs;

import cache.CacheUnit;
import cache.ItemCache;
import models.Item;
import models.Subscription;

import org.apache.commons.mail.EmailException;

import play.db.jpa.JPAPlugin;
import play.templates.Template;
import play.templates.TemplateLoader;
import utils.EmailUtil;

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

    public abstract String subject(Subscription subscriotion);

    public abstract int postCount();
    public abstract CacheUnit cacheUnit();
    public abstract void updateNextSendDate(Subscription subscription);

    public List<Item> getItems(){
        return ItemCache.getInstance().getItems(cacheUnit(), postCount());
    }

    protected void sendEmail(Subscription subscription, List<Item> itemList, String subject) throws EmailException {
        String htmlContent = createHtml(subscription, itemList, subject);
        String textContent = createText(subscription, itemList, subject);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("List-Unsubscribe", subscription.getUnSubsribeUrl());
        EmailUtil.sendEmail(htmlContent, textContent, subscription.getEmail(), subject, headers);

    }
    
    protected void sendEmail(List<Subscription> subscriptions, List<Item> itemList){
        for(Subscription subscription : subscriptions){
            try {
                sendEmail(subscription, itemList, subject(subscription));
                JPAPlugin.startTx(false);
                updateNextSendDate(subscription);
                subscription.save();
                JPAPlugin.closeTx(false);
            } catch (EmailException e) {
                e.printStackTrace();
            }
        }
    }


    private String createHtml(Subscription subscription, List<Item> itemList, String subject) {
        Template template = TemplateLoader.load("email/email.html");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("items", itemList);
        values.put("subject", subject);
        values.put("subscriber", subscription);
        return template.render(values);
    }

    private String createText(Subscription subscription, List<Item> itemList, String subject) {
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
        sb.append("Unsubscribe - ").append(subscription.getUnSubsribeUrl()).append("\n");
        sb.append("Modify Subscription - ").append(subscription.getModifyUrl()).append("\n");
        return sb.toString();
    }
}
