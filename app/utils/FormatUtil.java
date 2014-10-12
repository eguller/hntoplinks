package utils;

import java.text.DecimalFormat;

/**
 * User: eguller
 * Date: 4/15/14
 * Time: 6:06 AM
 */
public class FormatUtil {
    private static final ThreadLocal<DecimalFormat> decimalFormatThreadLocal = new ThreadLocal<DecimalFormat>() {
        @Override
        public DecimalFormat initialValue() {
          return new DecimalFormat(".000");
        }
    };

    public static String millis2Seconds(long millis) {
        double seconds = millis / 1000.0;
        return decimalFormatThreadLocal.get().format(seconds);
    }
}
