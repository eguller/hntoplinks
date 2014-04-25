package jobs;

import play.db.jpa.NoTransaction;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;

import java.util.List;

import email.EmailList;
import email.EmailListFactory;

/**
 * User: eguller
 * Date: 3/31/14
 * Time: 7:03 AM
 */

@Every("1h")
public class EmailSendJob extends Job {
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
