package jobs;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: eguller
 * Date: 11/10/13
 * Time: 11:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -Integer.MAX_VALUE);
        System.out.println(calendar.getTime());
    }
}
