package email;

import cache.ConfigCache;
import com.sendgrid.SendGrid;
import play.Logger;

import java.util.Map;

/**
 * User: eguller
 * Date: 5/1/16
 * Time: 1:09 PM
 */
public class SendGridProvider implements MailProvider {
    @Override
    public void sendEmail(String htmlContent, String textContent, String to, String subject, Map<String, String> headers) {
        String apiKey = ConfigCache.instance().getValue("sendgrid.key");
        SendGrid sendgrid = new SendGrid(apiKey);

        SendGrid.Email email = new SendGrid.Email();
        email.addTo(to);
        email.setFrom("toplinks@hntoplinks.com");
        email.setSubject(subject);
        email.setHtml(htmlContent);
        email.setText(textContent);
        email.setFromName("Hacker News Top Links");
        addHeaders(email, headers);

        try {
            SendGrid.Response response = sendgrid.send(email);
            if (!response.getStatus()) {
                Logger.error("Sending email failed recipient: %s, subject: %s, Message: %s", to, subject, response.getMessage());
                throw new RuntimeException("Sendgrid Response Code: " + response.getCode() + ", " +
                        "Sendgrid Response: " + response.getMessage());
            }
        } catch (Exception e) {
            Logger.error(e, "Sending email failed recipient: %s, subject: %s", to, subject);
            throw new RuntimeException(e);
        }
    }

    private void addHeaders(SendGrid.Email email, Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            email.addHeader(header.getKey(), header.getValue());
        }
    }
}
