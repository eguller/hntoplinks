package jobs;

import cache.CacheUnit;
import cache.ItemCache;
import models.Item;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.Play;
import play.db.jpa.NoTransaction;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;
import play.libs.Mail;
import play.vfs.VirtualFile;
import utils.Templater;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: eguller
 * Date: 3/31/14
 * Time: 7:03 AM
 */

@On("0 0 1 ? * *")
public class EmailSender extends Job {
    int sentEmailCount = 0;

    @Override
    @NoTransaction
    public void doJob() {
        List<EmailList> lists = EmailListFactory.getAllLists();
        for(EmailList list : lists){
            list.send();
        }
    }
}
