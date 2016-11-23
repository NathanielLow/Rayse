package com.rayse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by colby on 4/19/16.
 */
public class UserModel {
    public final static String DEFAULT_GOAL = "30";
    public final static String DEFAULT_START = "8";
    public final static String DEFAULT_END = "20";
    public final static boolean DEFAULT_VARY = false;

    static ParseUser currentUser = null;
    private static boolean useVaryingGoals = false;
    private static boolean useVaryingStartend = false;
    private static HashMap<Integer, Integer> starts;
    private static HashMap<Integer, Integer> ends;
    private static HashMap<Integer, Integer> goals;

    public static boolean hasCurrentUser() {
        return currentUser != null;
    }
    public static ParseUser getCurrentUser() {
        return currentUser;
    }
    public static boolean getUseVaryingGoals() {
        return useVaryingGoals;
    }
    public static boolean getUseVaryingStartEnd() {
        return useVaryingStartend;
    }
    public static HashMap<Integer, Integer> getStarts() {
        return starts;
    }
    public static HashMap<Integer, Integer> getEnds() {
        return ends;
    }
    public static HashMap<Integer, Integer> getGoals() {
        return goals;
    }

    public static void signup(final Context context, final String username, final String password) {
        boolean success;
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    UserModel.signin(context, username, password);
                } else {
                    Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public static void signin(final Context context, String username, String password) {
        try {
            currentUser = ParseUser.logIn(username, password);
            LightModel.reset();
            starts = new HashMap<Integer, Integer>();
            ends = new HashMap<Integer, Integer>();
            goals = new HashMap<Integer, Integer>();

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            Map<String, ?> allKeys = sp.getAll();

            for (Map.Entry<String, ?> entry : allKeys.entrySet()) {
                processKey(sp, entry.getKey());
            }
            // These starts + ends + goals of size 0 should only occur on FIRST INSTALL + RETURNING USER
            // This chunk is for pulling settings from the cloud
            // stringMap will be null if this is their first time ever using rayse
            HashMap<String, Integer> stringMap = (HashMap) currentUser.get("track_starts");
            if (starts.size() < Utils.DAYS.length && stringMap != null) {
                if (stringMap != null) {
                    for (String k : stringMap.keySet()) {
                        starts.put(Integer.valueOf(k), stringMap.get(k));
                    }
                    stringMap = (HashMap) currentUser.get("track_ends");
                    for (String k : stringMap.keySet()) {
                        ends.put(Integer.valueOf(k), stringMap.get(k));
                    }
                    stringMap = (HashMap) currentUser.get("goals");
                    for (String k : stringMap.keySet()) {
                        goals.put(Integer.valueOf(k), stringMap.get(k));
                    }
                }
            }
            // Just initialize to defaults if first time ever
            if (starts.size() < Utils.DAYS.length && stringMap == null) {
                PreferenceManager.setDefaultValues(context, R.xml.preferences, false);

                allKeys = sp.getAll();
                for (Map.Entry<String, ?> entry : allKeys.entrySet()) {
                    processKey(sp, entry.getKey());
                }
            }

            if (context instanceof TutorialActivity) {
                Intent sendGoalIntent = new Intent(context, PhoneToWatchService.class);
                sendGoalIntent.putExtra("COMMAND", "pushData");
                context.startService(sendGoalIntent);

                TutorialActivity activity = (TutorialActivity) context;
                activity.enterApp();
            }
        } catch (ParseException e) {
            Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Fill in the UserModel data structs based on prefs and key
    public static void processKey(SharedPreferences sp, String key) {
        if (key.equals("use_varying_goals")) {
            useVaryingGoals = Boolean.valueOf(sp.getBoolean(key, DEFAULT_VARY));
            if (useVaryingGoals) {
                for (String keySubstring : Utils.KEYSTRINGS) {
                    processKey(sp, keySubstring + "_goal");
                }
            } else {
                processKey(sp, "global_goal");
            }
        } else if (key.equals("use_varying_startend")) {
            useVaryingStartend = Boolean.valueOf(sp.getBoolean(key, DEFAULT_VARY));
            if (useVaryingStartend) {
                for (String keySubstring : Utils.KEYSTRINGS) {
                    processKey(sp, keySubstring + "_start");
                }
                for (String keySubstring : Utils.KEYSTRINGS) {
                    processKey(sp, keySubstring + "_end");
                }
            } else {
                processKey(sp, "global_start");
                processKey(sp, "global_end");
            }
        } else if (key.equals("global_goal")) {
            if (!useVaryingGoals) {
                int goal = Integer.valueOf(sp.getString(key, DEFAULT_GOAL));
                fillGlobalMapByDay(UserModel.goals, goal);
            }
        } else if (key.equals("global_start")) {
            if (!useVaryingStartend) {
                int start = Integer.valueOf(sp.getString(key, DEFAULT_START));
                fillGlobalMapByDay(UserModel.starts, start);
            }
        } else if (key.equals("global_end")) {
            if (!useVaryingStartend) {
                int end = Integer.valueOf(sp.getString(key, DEFAULT_END));
                fillGlobalMapByDay(UserModel.ends, end);
            }
        } else if (key.startsWith("day") && key.endsWith("goal")) {
            if (useVaryingGoals) {
                int goal = Integer.valueOf(sp.getString(key, DEFAULT_GOAL));
                UserModel.goals.put(Utils.keyToDay(key.substring(0, 7)), goal);
            }
        } else if (key.startsWith("day") && key.endsWith("start")) {
            if (useVaryingStartend) {
                int start = Integer.valueOf(sp.getString(key, DEFAULT_START));
                UserModel.starts.put(Utils.keyToDay(key.substring(0, 7)), start);
            }
        } else if (key.startsWith("day") && key.endsWith("end")) {
            if (useVaryingStartend) {
                int end = Integer.valueOf(sp.getString(key, DEFAULT_END));
                UserModel.ends.put(Utils.keyToDay(key.substring(0, 7)), end);
            }
        }
    }

    private static boolean fillMapFromJSON(HashMap<Integer, Integer> map, JSONObject json) {
        try {
            for (int day : Utils.DAYS) {
                String jsonKey = Integer.toString(day);
                map.put(day, json.getInt(jsonKey));
            }
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private static void fillGlobalMapByDay(HashMap<Integer, Integer> map, int value) {
        for (int day: Utils.DAYS) {
            map.put(day, value);
        }
    }

    public static void logout() {
        if (currentUser != null) {
            currentUser.logOut();
            currentUser = null;
        }
    }

    public static void save(final Context context) {
        try {
            // Honestly the dumbest thing I've ever written
            HashMap<String, Integer> jsonableStarts = new HashMap<>();
            HashMap<String, Integer> jsonableEnds = new HashMap<>();
            HashMap<String, Integer> jsonableGoals = new HashMap<>();
            for(int k: starts.keySet()) {
                jsonableStarts.put(Integer.toString(k), starts.get(k));
            }
            for(int k: ends.keySet()) {
                jsonableEnds.put(Integer.toString(k), ends.get(k));
            }
            for(int k: goals.keySet()) {
                jsonableGoals.put(Integer.toString(k), goals.get(k));
            }
            JSONObject startsObj = new JSONObject(jsonableStarts);
            JSONObject endsObj = new JSONObject(jsonableEnds);
            JSONObject goalsObj = new JSONObject(jsonableGoals);
            currentUser.put("track_starts", startsObj);
            currentUser.put("track_ends", endsObj);
            currentUser.put("goals", goalsObj);
            currentUser.save();
        } catch (ParseException e) {
            Toast toast = Toast.makeText(context, "Failed to save user settings", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void setStarts(HashMap<Integer,Integer> starts) {
        if (currentUser != null) {
            UserModel.starts = starts;
        } else {
            Log.d("UserModel", "If you see this the impossible has occurred");
        }
    }

    public static void setEnds(HashMap<Integer,Integer> ends) {
        if (currentUser != null) {
            UserModel.ends = ends;
        } else {
            Log.d("UserModel", "If you see this the impossible has occurred");
        }
    }

    public static void setGoals(HashMap<Integer, Integer> goals) {
        UserModel.goals = goals;
    }

    public static void setUseVaryingGoals(boolean value) {
        useVaryingGoals = value;
    }

    public static void setUseVaryingStartend(boolean value) {
        useVaryingStartend = value;
    }
}
