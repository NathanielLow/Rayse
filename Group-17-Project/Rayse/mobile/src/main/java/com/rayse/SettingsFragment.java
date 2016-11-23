package com.rayse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by colby on 4/28/16.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    public final static String BROADCAST = SettingsFragment.class.getName() + "GoalBroadcast";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        Map<String, ?> allKeys = sp.getAll();

        for (Map.Entry<String,?> entry: allKeys.entrySet()) {
            updateSummary(sp, entry.getKey());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("SettingsFragment", "got changed on " + key);
        updateSummary(sharedPreferences, key);
        UserModel.save(getActivity());

    }

    private void updateSummary(SharedPreferences sharedPreferences, String key) {
        if (key.startsWith("day") || key.startsWith("global")) {
            Preference dayPref = findPreference(key);
            if (key.endsWith("goal")) {
                if (validateInt(sharedPreferences.getString(key, ""))) {
                    dayPref.setSummary(sharedPreferences.getString(key, "") + " minutes");
                    UserModel.processKey(sharedPreferences, key);

                    // On goal update, let the watch know
                    Intent sendGoalIntent = new Intent(getActivity(), PhoneToWatchService.class);
                    sendGoalIntent.putExtra("COMMAND", "pushData");
                    getActivity().startService(sendGoalIntent);
                    updateGoalBroadcast();
                } else {
                    sharedPreferences.edit().putString(key, UserModel.DEFAULT_GOAL);
                }
            } else if (key.endsWith("start") || key.endsWith("end")) {
                if (validateTime(sharedPreferences.getString(key, ""))) {
                    dayPref.setSummary(sharedPreferences.getString(key, "") + ":00");
                    UserModel.processKey(sharedPreferences, key);
                } else {
                    if (key.endsWith("start")) {
                        sharedPreferences.edit().putString(key, UserModel.DEFAULT_START);
                    } else {
                        sharedPreferences.edit().putString(key, UserModel.DEFAULT_END);
                    }
                }
            }
        } else if (key.startsWith("use")) {
            UserModel.processKey(sharedPreferences, key);
        }
    }

    private void updateGoalBroadcast() {
        Intent intent = new Intent(BROADCAST);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private boolean validateInt(String time) {
        int hour;
        try {
            hour = Integer.parseInt(time);
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.settings_bad_time), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    private boolean validateTime(String time) {
        if (!validateInt(time)) {
            return false;
        }
        int hour = Integer.valueOf(time);
        if (hour < 0 || hour > 23) {
            Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.settings_bad_time), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
