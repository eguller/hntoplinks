package email;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.libs.Mail;

import java.util.Map;

/**
 * User: eguller
 * Date: 5/1/16
 * Time: 1:18 PM
 */
public class PlayEmailProvider implements MailProvider {
    @Override
    public void sendEmail(String htmlContent, String textContent, String to, String subject, Map<String, String> headers) {
        HtmlEmail email = new HtmlEmail();
        try {
            email.setFrom("toplinks@hntoplinks.com", "Hacker News Top Links");
            email.setSubject(subject);
            email.setHtmlMsg(htmlContent);
            email.setTextMsg(textContent);
            email.setHeaders(headers);
            Mail.send(email);
            email.addTo(to);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
