package com.rayse;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Created by colby on 4/23/16.
 */
public class LightObject {
    private final int NUM_HOURS = 24;
    private String userId;
    private Calendar day;
    private int[] timeseries;
    private ParseObject lightObject;
    private HashMap<String, String> timeRanges;

    public String getUserId() {
        return userId;
    }

    public Calendar getDay() {
        return day;
    }

    public int[] getTimeseries() {
        return timeseries;
    }

    public ParseObject getParseObject() {
        return lightObject;
    }

    public LightObject() {
        userId = UserModel.getCurrentUser().getObjectId();
        Calendar dummyDay = Calendar.getInstance();
        day = new GregorianCalendar(dummyDay.get(Calendar.YEAR), dummyDay.get(Calendar.MONTH), dummyDay.get(Calendar.DAY_OF_MONTH));

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Light");
        query.whereEqualTo("userId", userId);
        query.whereEqualTo("date", day.getTime());
        ParseObject existing;
        try {
            existing = query.getFirst();
            fromParseObject(existing);
        } catch (ParseException e) {
            // Object not found
            Log.d("LightObject", "Constructor parseexception: " + e.getMessage());
            lightObject = new ParseObject("Light");
            lightObject.put("userId", userId);
            lightObject.put("date", day.getTime());
            timeseries = new int[24];
            for (int i = 0; i < timeseries.length; i++ ) {
                timeseries[i] = 0;
            }
            timeRanges = new HashMap<>();
            save();
        }

    }

    public LightObject(ParseObject obj) {
        fromParseObject(obj);
    }

    private void fromParseObject(ParseObject obj) {
        lightObject = obj;
        Log.d("LightObject", "Reconstructing from " + lightObject.getObjectId());
        userId = obj.getString("userId");

        day = Calendar.getInstance();
        day.setTime(obj.getDate("date"));

        JSONArray jsonTimeseries = obj.getJSONArray("timeseries");
        timeseries = new int[NUM_HOURS];
        if (jsonTimeseries != null ) {
            try {
                for (int i = 0; i < jsonTimeseries.length(); i++) {
                    timeseries[i] = jsonTimeseries.getInt(i);
                }
            } catch (JSONException e) {
                Log.d("LightObject", "************************ TIMESERIES RECONSTRUCTION: " + e.getMessage());
            }
        } else {
            for (int i = 0; i < timeseries.length; i++ ) {
                timeseries[i] = 0;
            }
        }

        JSONObject jsonTimeranges = obj.getJSONObject("timeRanges");
        timeRanges = new HashMap<>();
        if (jsonTimeranges != null ) {
            Iterator keyset = jsonTimeranges.keys();
            try {
                while (keyset.hasNext()) {
                    String key = (String) keyset.next();
                    String value = (String) jsonTimeranges.get(key);
                    timeRanges.put(key, value);
                }
            } catch (JSONException e) {
                Log.d("LightObject", "************************ TIMERANGES RECONSTRUCTION: " + e.getMessage());
            }
        }
    }

    public void addLightToHour(int hour, int light) {
        timeseries[hour] += light;
    }

    public void putTimeRange(String start, String end) {
        timeRanges.put(start, end);
    }

    public void setTimeseries(int[] timeseries)
    {
        this.timeseries = timeseries;
        try {
            lightObject.put("timeseries", new JSONArray(timeseries));
        } catch (JSONException e) {
            Log.d("LightObject", "************************ UNABLE TO PUT TIMESERIES: " + e.getMessage());
        }
    }

    public void setTimeRanges(HashMap<String, String> timeRanges) {
        this.timeRanges = timeRanges;
        lightObject.put("timeRanges", new JSONObject(timeRanges));
    }

    public void __setDay(Calendar day) {
        this.day = day;
        lightObject.put("date", day.getTime());
    }

    public void save() {
        JSONArray jsonTimeseries = null;
        JSONObject jsonTimeRanges = new JSONObject(timeRanges);
        try {
            jsonTimeseries = new JSONArray(timeseries);
            lightObject.put("timeseries", jsonTimeseries);
        } catch (JSONException e) {
            Log.d("LightObject", "************************ UNABLE TO PUT TIMESERIES: " + e.getMessage());
        }
        lightObject.put("timeRanges", jsonTimeRanges);
        try {
            lightObject.save();
        } catch (ParseException e) {
            Log.d("LightObject", "*************** UNABLE TO SAVE TO DB " + e.getMessage());
        }
        Log.d("LightObject", "Saved with timeseries " + jsonTimeseries + " and timeranges " + jsonTimeRanges);
    }
}
