package email;

import cache.ConfigCache;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import play.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * User: eguller
 * Date: 5/1/16
 * Time: 1:03 PM
 */
public class MandrillAppProvider implements MailProvider {

    @Override
    public void sendEmail(String htmlContent, String textContent, String to, String subject, Map<String, String> headers) {
        String apiKey = ConfigCache.instance().getValue("mandrillapi.key");
        MandrillApi mandrillApi = new MandrillApi(apiKey);
        MandrillMessage message = new MandrillMessage();
        message.setSubject(subject);
        message.setHtml(htmlContent);
        message.setAutoText(true);
        message.setFromEmail("toplinks@hntoplinks.com");
        message.setFromName("Hacker News Top Links");
        message.setHeaders(headers);
        message.setText(textContent);
        ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(to);
        recipients.add(recipient);
        message.setTo(recipients);
        message.setPreserveRecipients(true);
        try {
            MandrillMessageStatus[] messageStatusReports = mandrillApi
                    .messages().send(message, false);
            for (MandrillMessageStatus status : messageStatusReports) {
                Logger.info("Message sent. id: %s, email: %s, status: %s, rejection reason: %s", status.getId(), status.getEmail(), status.getStatus(), status.getRejectReason());
            }
        } catch (MandrillApiError mandrillApiError) {
            mandrillApiError.printStackTrace();
            Logger.error(mandrillApiError, "Sending email failed recipient: %s, subject: %s", to, subject);
        } catch (IOException e) {
            Logger.error(e, "Sending email failed recipient: %s, subject: %s", to, subject);
        }

    }
}
