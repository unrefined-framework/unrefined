package unrefined.util;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

public class TimeZones {

    private TimeZones() {
        throw new NotInstantiableError(TimeZones.class);
    }

    public static boolean isSupported(String timeZoneName) {
        for (String string : TimeZone.getAvailableIDs()) {
            if (string.equals(timeZoneName)) return true;
        }
        if (timeZoneName.length() >= 5 && timeZoneName.startsWith("GMT")) {
            return !TimeZone.getTimeZone(timeZoneName).getID().equals("GMT");
        }
        return false;
    }

    public static TimeZone forName(String timeZoneName) {
        return forName(timeZoneName, null);
    }

    public static TimeZone forName(String timeZoneName, TimeZone fallback) {
        for (String string : TimeZone.getAvailableIDs()) {
            if (string.equals(timeZoneName)) return TimeZone.getTimeZone(timeZoneName);
        }
        if (timeZoneName.length() >= 5 && timeZoneName.startsWith("GMT")) {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneName);
            if (!timeZone.getID().equals("GMT")) return timeZone;
        }
        return fallback;
    }

    public static SortedMap<String, TimeZone> availableTimeZones() {
        TreeMap<String, TimeZone> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String timeZoneName : TimeZone.getAvailableIDs()) {
            map.put(timeZoneName, TimeZone.getTimeZone(timeZoneName));
        }
        return Collections.unmodifiableSortedMap(map);
    }

    public static TimeZone defaultTimeZone() {
        return TimeZone.getDefault();
    }

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final TimeZone GMT_MINUS_12 = TimeZone.getTimeZone("Etc/GMT-12");
    public static final TimeZone GMT_MINUS_11 = TimeZone.getTimeZone("Etc/GMT-11");
    public static final TimeZone GMT_MINUS_10 = TimeZone.getTimeZone("Etc/GMT-10");
    public static final TimeZone GMT_MINUS_9 = TimeZone.getTimeZone("Etc/GMT-9");
    public static final TimeZone GMT_MINUS_8 = TimeZone.getTimeZone("Etc/GMT-8");
    public static final TimeZone GMT_MINUS_7 = TimeZone.getTimeZone("Etc/GMT-7");
    public static final TimeZone GMT_MINUS_6 = TimeZone.getTimeZone("Etc/GMT-6");
    public static final TimeZone GMT_MINUS_5 = TimeZone.getTimeZone("Etc/GMT-5");
    public static final TimeZone GMT_MINUS_4 = TimeZone.getTimeZone("Etc/GMT-4");
    public static final TimeZone GMT_MINUS_3 = TimeZone.getTimeZone("Etc/GMT-3");
    public static final TimeZone GMT_MINUS_2 = TimeZone.getTimeZone("Etc/GMT-2");
    public static final TimeZone GMT_MINUS_1 = TimeZone.getTimeZone("Etc/GMT-1");
    public static final TimeZone GMT_MINUS_0 = TimeZone.getTimeZone("Etc/GMT-0");
    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    public static final TimeZone GMT_PLUS_0 = TimeZone.getTimeZone("Etc/GMT+0");
    public static final TimeZone GMT_PLUS_1 = TimeZone.getTimeZone("Etc/GMT+1");
    public static final TimeZone GMT_PLUS_2 = TimeZone.getTimeZone("Etc/GMT+2");
    public static final TimeZone GMT_PLUS_3 = TimeZone.getTimeZone("Etc/GMT+3");
    public static final TimeZone GMT_PLUS_4 = TimeZone.getTimeZone("Etc/GMT+4");
    public static final TimeZone GMT_PLUS_5 = TimeZone.getTimeZone("Etc/GMT+5");
    public static final TimeZone GMT_PLUS_6 = TimeZone.getTimeZone("Etc/GMT+6");
    public static final TimeZone GMT_PLUS_7 = TimeZone.getTimeZone("Etc/GMT+7");
    public static final TimeZone GMT_PLUS_8 = TimeZone.getTimeZone("Etc/GMT+8");
    public static final TimeZone GMT_PLUS_9 = TimeZone.getTimeZone("Etc/GMT+9");
    public static final TimeZone GMT_PLUS_10 = TimeZone.getTimeZone("Etc/GMT+10");
    public static final TimeZone GMT_PLUS_11 = TimeZone.getTimeZone("Etc/GMT+11");
    public static final TimeZone GMT_PLUS_12 = TimeZone.getTimeZone("Etc/GMT+12");

}
