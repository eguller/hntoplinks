package utils;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.Logger;
import play.libs.Mail;
import play.templates.Template;
import play.templates.TemplateLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: eguller
 * Date: 4/7/14
 * Time: 11:41 PM
 */
public class EmailUtil {
    private static final Map<String, String> EMPTY_HEADER = new HashMap<String, String>();
    public static void sendEmail(String htmlContent, String textContent, String to, String subject) throws EmailException {
        sendEmail(htmlContent, textContent, to, subject, EMPTY_HEADER);
    }

    public static void sendEmail(String htmlContent, String textContent, String to, String subject, Map<String, String> headers) throws EmailException{
        sendMandrillAppMail(htmlContent, textContent, to, subject, headers);
    }

    private static void sendEmailPlay(String htmlContent, String textContent, String to, String subject, Map<String, String> headers) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.addTo(to);
        email.setFrom("toplinks@hntoplinks.com", "Hacker News Top Links");
        email.setSubject(subject);
        email.setHtmlMsg(htmlContent);
        email.setTextMsg(textContent);
        email.setHeaders(headers);
        Mail.send(email);
    }

    private static void sendMandrillAppMail(String htmlContent, String textContent, String to, String subject, Map<String, String> headers) {
        String apiKey = System.getProperty("mandrillapi.key");
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
            for(MandrillMessageStatus status : messageStatusReports) {
                Logger.info("Message sent. id: %s, email: %s, status: %s, rejection reason: %s",status.getId(), status.getEmail(), status.getStatus(), status.getRejectReason());
            }
        } catch (MandrillApiError mandrillApiError) {
            mandrillApiError.printStackTrace();
            Logger.error(mandrillApiError, "Sending email failed recipient: %s, subject: %s", to, subject);
        } catch (IOException e) {
            Logger.error(e, "Sending email failed recipient: %s, subject: %s", to, subject);
        }
    }

    public static void sendActivationEmail(Subscription subscription, String to) throws EmailException {
        Template emailHtmlTemplate = TemplateLoader.load("email/activation_email.html");
        Template emailTxtTemplate = TemplateLoader.load("email/activation_email.txt");
        Map<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put("subscription", subscription);
        String htmlContent = emailHtmlTemplate.render(templateValues);
        String textContent = emailTxtTemplate.render(templateValues);
       sendEmail(htmlContent, textContent, to, "Email Verification");
    }

}
