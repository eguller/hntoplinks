package jobs;

import email.EmailList;
import email.EmailListFactory;
import play.db.jpa.NoTransaction;
import play.jobs.Every;
import play.jobs.Job;

import java.util.List;

/**
 * User: eguller
 * Date: 3/31/14
 * Time: 7:03 AM
 */

@Every("1h")
public class EmailSendJob extends Job {

  @Override
  @NoTransaction
  public void doJob() {
    List<EmailList> lists = EmailListFactory.getAllLists();
    for (EmailList list : lists) {
      list.send();
    }
  }
}
