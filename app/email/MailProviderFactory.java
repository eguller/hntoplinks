package email;

/**
 * User: eguller
 * Date: 5/1/16
 * Time: 1:15 PM
 */
public class MailProviderFactory {
  public static MailProvider getMailProvider() {
    return new SendGridProvider();
  }
}
