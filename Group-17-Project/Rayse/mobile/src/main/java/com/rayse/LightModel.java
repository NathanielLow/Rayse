package com.rayse;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by colby on 4/27/16.
 */
public class LightModel {
    private static LightObject today = null;
    private static List<LightObject> cachedLight;
    private static Calendar cachedStart;
    private static Calendar cachedEnd;

    public static LightObject today() {
        if (today == null) {
            today = new LightObject();
        }
        return today;
    }

    public static void reset() {
        today = null;
        cachedLight = null;
        cachedStart = null;
        cachedEnd = null;
    }

    /* Calendar arguments must be constructed with default hours/minutes/sec/ms
       Invariant: all of the cached* variables are null or non-null
     */
    public static List<LightObject> getLightBetweenDays(Context context, Calendar start, Calendar end) {
        if (cachedLight != null && cachedStart.compareTo(start) <= 0 && end.compareTo(cachedEnd) <= 0) {
            if (cachedLight.size() == 0) {
                return cachedLight;
            }
            int startIdx = -1;
            int endIdx = -1;
            for (int i = 0; i < cachedLight.size(); i++) {
                if (cachedLight.get(i).getDay().compareTo(start) >= 0) {
                    startIdx = i;
                    break;
                }
            }
            for (int i = cachedLight.size(); i >= 0; i++) {
                if (cachedLight.get(i).getDay().compareTo(end) <= 0) {
                    endIdx = i + 1;
                    break;
                }
            }
            if (startIdx == -1 || endIdx == -1) {
                Toast toast = Toast.makeText(context, "getLightBetweenDays IMPOSSIBLE HAS OCCURRED", Toast.LENGTH_SHORT);
                toast.show();
                return null;
            }
            return cachedLight.subList(startIdx, endIdx);
        } else {
            cachedStart = start;
            cachedEnd = end;
            cachedLight = new ArrayList<>();
            List<ParseObject> parseLights;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Light");
            query.whereEqualTo("userId", UserModel.getCurrentUser().getObjectId());
            query.whereGreaterThanOrEqualTo("date", start.getTime());
            query.whereLessThanOrEqualTo("date", end.getTime());
            try {
                parseLights = query.find();
            } catch (ParseException e) {
                // Objects not found
                return cachedLight;
            }
            Calendar dummyDay = Calendar.getInstance();
            Calendar midnightToday = new GregorianCalendar(dummyDay.get(Calendar.YEAR), dummyDay.get(Calendar.MONTH), dummyDay.get(Calendar.DAY_OF_MONTH));
            for (ParseObject parseLight: parseLights) {
                LightObject temp = new LightObject(parseLight);
                if (temp.getDay().get(Calendar.HOUR_OF_DAY) == midnightToday.get(Calendar.HOUR_OF_DAY)) {
                    cachedLight.add(temp);
                }
            }
            return cachedLight;
        }
    }
}
