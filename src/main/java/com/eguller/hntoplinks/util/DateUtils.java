package com.eguller.hntoplinks.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static String since(LocalDateTime time){
        long yearsBetween = ChronoUnit.YEARS.between(time, LocalDateTime.now());
        if(yearsBetween == 1){
            return yearsBetween  + " year ago";
        } else if(yearsBetween > 1){
            return yearsBetween  + " years ago";
        }

        long monthsBetween = ChronoUnit.MONTHS.between(time, LocalDateTime.now());
        if(monthsBetween == 1){
            return monthsBetween  + " month ago";
        } else if(monthsBetween > 1){
            return monthsBetween  + " months ago";
        }

        long weeksBetween = ChronoUnit.WEEKS.between(time, LocalDateTime.now());
        if(weeksBetween == 1){
            return yearsBetween  + " week ago";
        } else if(weeksBetween > 1){
            return yearsBetween  + " weeks ago";
        }

        long dasysBetween = ChronoUnit.DAYS.between(time, LocalDateTime.now());
        if(dasysBetween == 1){
            return dasysBetween  + " day ago";
        } else if(dasysBetween > 1){
            return dasysBetween  + " days ago";
        }

        long hoursBetween = ChronoUnit.HOURS.between(time, LocalDateTime.now());
        if(hoursBetween == 1){
            return hoursBetween  + " hour ago";
        } else if(hoursBetween > 1){
            return hoursBetween  + " hours ago";
        }

        long minutesBetween = ChronoUnit.MINUTES.between(time, LocalDateTime.now());
        if(minutesBetween == 1){
            return minutesBetween  + " minute ago";
        } else if(minutesBetween > 1){
            return minutesBetween  + " minutes ago";
        } else {
            return "just now";
        }
    }
}
