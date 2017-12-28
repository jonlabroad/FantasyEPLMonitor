package util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Date {
    public static final DateTimeFormatter _formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss z");
    public static final DateTimeFormatter _apiFormatter = DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ss'Z'").withZone(DateTimeZone.forID("Europe/London"));

    public static String toString(DateTime time) {
        return _formatter.print(time);
    }

    public static DateTime fromString(String dateString) {
        return _formatter.parseDateTime(dateString);
    }

    public static DateTime fromApiString(String dateString) {
        return _apiFormatter.parseDateTime(dateString);
    }
}
