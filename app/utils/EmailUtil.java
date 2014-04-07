package utils;

import models.Subscription;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.Play;
import play.libs.Mail;
import play.vfs.VirtualFile;

import java.util.HashMap;
import java.util.Map;

/**
 * User: eguller
 * Date: 4/7/14
 * Time: 11:41 PM
 */
public class EmailUtil {
    public static void sendEmail(String htmlContent, String textContent, String to, String subject) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.addTo(to);
        email.setFrom("toplinks@hntoplinks.com", "Hacker News Top Links");
        email.setSubject(subject);
        email.setHtmlMsg(htmlContent);
        email.setTextMsg(textContent);
        Mail.send(email);
    }

    public static void sendActivationEmail(Subscription subscription, String to) throws EmailException {
        String htmlContent = VirtualFile.fromRelativePath("/app/template/activation_email.html").contentAsString();
        String textContent = VirtualFile.fromRelativePath("/app/template/activation_email.html").contentAsString();
        Map<String, String> values = new HashMap<String, String>();
        values.put("applicationBaseUrl", Play.configuration.getProperty("application.baseUrl"));
        values.put("subscriptionid", subscription.getSubsUUID());
        htmlContent = Templater.template(htmlContent, values);
        textContent = Templater.template(textContent, values);
       sendEmail(htmlContent, textContent, to, "Email Verification");
    }

}
