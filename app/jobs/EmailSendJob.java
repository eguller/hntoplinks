package jobs;

import play.db.jpa.NoTransaction;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;

import java.util.List;

/**
 * User: eguller
 * Date: 3/31/14
 * Time: 7:03 AM
 */

@Every("10mn")
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
