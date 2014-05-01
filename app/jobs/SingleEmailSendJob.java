package jobs;

import email.NextSendDateUpdater;
import models.Item;
import models.StatisticsMgr;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.jobs.Job;
import play.templates.Template;
import play.templates.TemplateLoader;
import utils.EmailUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: eguller
 * Date: 4/27/14
 * Time: 11:49 AM
 */
public class SingleEmailSendJob extends Job<Boolean> {
    Subscription subscription;
    List<Item> itemList;
    NextSendDateUpdater nextSendDateUpdater;
    String subject;

    public SingleEmailSendJob(Subscription subscription, List<Item> itemList, String subject, NextSendDateUpdater nextSendDateUpdater) {
        this.subscription = subscription;
        this.itemList = itemList;
        this.subject = subject;
        this.nextSendDateUpdater = nextSendDateUpdater;
    }

    @Override
    public Boolean doJobWithResult() {
        try {
            sendEmail(subscription, itemList, subject);
            nextSendDateUpdater.updateNextSendDate(subscription);
        } catch (EmailException e) {
            Logger.error(e, "Sending email to %s failed.", subscription.getEmail());
            StatisticsMgr.instance().incrementFailedMailCount();
            return false;
        } catch (Exception ex){
            //maybe email is sent but there is another error. to prevent same email again and again
            //return true even if it is error.
            Logger.error(ex, "Exception occured while sending email to %s", subscription.getEmail());
            StatisticsMgr.instance().incrementFailedMailCount();
            return true;
        }
        StatisticsMgr.instance().incrementSuccessMailCount();
        return true;
    }

    protected void sendEmail(Subscription subscription, List<Item> itemList,
                             String subject) throws EmailException {
        String htmlContent = createHtml(subscription, itemList, subject);
        String textContent = createText(subscription, itemList, subject);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("List-Unsubscribe", subscription.getUnSubsribeUrl());
        EmailUtil.sendEmail(htmlContent, textContent, subscription.getEmail(),
                subject, headers);

    }

    private String createHtml(Subscription subscription, List<Item> itemList,
                              String subject) {
        Template template = TemplateLoader.load("email/email.html");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("items", itemList);
        values.put("subject", subject);
        values.put("subscriber", subscription);
        return template.render(values);
    }

    private String createText(Subscription subscription, List<Item> itemList,
                              String subject) {
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
        sb.append("Unsubscribe - ").append(subscription.getUnSubsribeUrl())
                .append("\n");
        sb.append("Modify Subscription - ").append(subscription.getModifyUrl())
                .append("\n");
        return sb.toString();
    }
}
