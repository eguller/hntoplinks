package jobs;

import models.Item;
import play.Play;
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

@On("0 0 1 ? * *")
public class EmailSender extends Job{
    private static final
    int previousDay = -1;
    int previousWeek = -1;
    int sentEmailCount = 0;
    @Override @NoTransaction
    public void doJob(){
          String sender = Play.configuration.getProperty("mail.smtp.user");
    }

    public void sendEmail(String to){

    }

    public String createHtml(List<Item> itemList){
        return null;
    }

    public String createText(List<Item> itemList){
        return null;
    }
}
