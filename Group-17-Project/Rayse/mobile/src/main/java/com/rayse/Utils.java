package com.rayse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by colby on 4/19/16.
 */
public class Utils {
    public static int[] DAYS = {Calendar.SUNDAY,
                                Calendar.MONDAY,
                                Calendar.TUESDAY,
                                Calendar.WEDNESDAY,
                                Calendar.THURSDAY,
                                Calendar.FRIDAY,
                                Calendar.SATURDAY};
    public static HashMap<String, Integer> keyToDay = null;
    public static DateFormat df = null;
    public static String[] KEYSTRINGS = {"day_sun", "day_mon", "day_tue", "day_wed", "day_thu", "day_fri", "day_sat"};

    public static int keyToDay(String key) {
        if (keyToDay == null) {
            keyToDay = new HashMap<>();
            for (int i = 0; i < DAYS.length; i++) {
                keyToDay.put(KEYSTRINGS[i], DAYS[i]);
            }
        }
        return keyToDay.get(key);
    }

    public static String isoFormat(Date d) {
        if (df == null) {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
        }
        return df.format(d);
    }
}
