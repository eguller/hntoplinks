package utils;

import play.vfs.VirtualFile;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: eguller
 * Date: 4/1/14
 * Time: 7:01 AM
 */
public class Templater {
    public static String template(String content, Map<String, String> values) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(content);

        //populate the replacements map ...
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = values.get(matcher.group(1));
            builder.append(content.substring(i, matcher.start()));
            if (replacement == null) {
                builder.append(matcher.group(0));
            } else {
                builder.append(replacement);
            }
            i = matcher.end();
        }
        builder.append(content.substring(i, content.length()));
        return builder.toString();
    }
}
