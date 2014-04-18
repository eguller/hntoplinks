package jobs;

import play.db.jpa.NoTransaction;
import play.jobs.Job;
import play.jobs.On;

import java.util.*;

/**
 * User: eguller
 * Date: 3/31/14
 * Time: 7:03 AM
 */

@On("0 0 1 ? * *")
//@Every("1h")
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
