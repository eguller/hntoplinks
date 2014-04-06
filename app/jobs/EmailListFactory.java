package jobs;

import java.util.ArrayList;
import java.util.List;

/**
 * User: eguller
 * Date: 4/6/14
 * Time: 11:51 PM
 */
public class EmailListFactory {
    public static List<EmailList> getAllLists(){
        List<EmailList> lists = new ArrayList<EmailList>();
        lists.add(new DailyMailList());
        lists.add(new WeeklyMailList());
        lists.add(new MonthlyMailList());
        lists.add(new AnnuallyMailList());
        return lists;
    }
}
