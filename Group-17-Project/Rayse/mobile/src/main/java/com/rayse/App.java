package com.rayse;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by colby on 4/19/16.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, BuildConfig.PARSE_ID, BuildConfig.PARSE_CLIENT_KEY);
    }
}
