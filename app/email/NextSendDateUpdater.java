package email;

import models.Subscription;

/**
 * User: eguller
 * Date: 4/27/14
 * Time: 11:50 AM
 */
public interface NextSendDateUpdater {
    public abstract void updateNextSendDate(Subscription subscription);
}
