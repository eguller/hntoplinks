package email;

import java.util.Map;

/**
 * User: eguller
 * Date: 5/1/16
 * Time: 1:02 PM
 */
public interface MailProvider {
    public void sendEmail(String htmlContent, String textContent, String to, String subject, Map<String, String> headers);
}
