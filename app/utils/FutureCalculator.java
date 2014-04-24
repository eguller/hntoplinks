package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * User: eguller
 * Date: 4/24/14
 * Time: 6:19 AM
 */
public class FutureCalculator {
    private static final int SEND_HOUR = 7;
    TimeZone timeZone;
    public FutureCalculator(TimeZone timeZone){
        this.timeZone = timeZone;
    }
    
    public FutureCalculator(String timeZone){
    	this.timeZone = TimeZone.getTimeZone(timeZone);
    }

    public Date getNextDay(){
        Calendar calendar = Calendar.getInstance(timeZone);
        setHour(calendar);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    public Date getNextWeek(){
        Calendar calendar = Calendar.getInstance(timeZone);
        setHour(calendar);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        return calendar.getTime();
    }

    public Date getNextMonth(){
        Calendar calendar = Calendar.getInstance(timeZone);
        setHour(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    public Date getNextYear(){
        Calendar calendar = Calendar.getInstance(timeZone);
        setHour(calendar);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.add(Calendar.YEAR, 1);
        calendar.getTime();
        return calendar.getTime();
    }

    private void setHour(Calendar calendar){
        calendar.set(Calendar.HOUR, SEND_HOUR);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    
    public static void main(String[] args){
    	TimeZone defaultTimeZone = TimeZone.getDefault();
    	System.out.println(defaultTimeZone);
    	TimeZone timeZone = TimeZone.getTimeZone("Etc/GMT-1");
    	FutureCalculator futureCalculator = new FutureCalculator(timeZone);
    	System.out.println(futureCalculator.getNextDay());
    	System.out.println(futureCalculator.getNextWeek());
    	System.out.println(futureCalculator.getNextMonth());
    	System.out.println(futureCalculator.getNextYear());
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss");
    	Date date = Calendar.getInstance().getTime();
    	System.out.println(sdf.format(date));
    	Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+6"));
    	cl.setTime(date);
    	sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT+6"));
    	System.out.println(sdf.format(cl.getTime()));
    }
}
