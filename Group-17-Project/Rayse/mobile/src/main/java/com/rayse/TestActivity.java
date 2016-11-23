package com.rayse;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

// TODO: Delete. This class is to test if refactor broke things
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Fragment fragment = new NowFragment();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.test_content_frame, fragment).commit();
        setTitle("TEST");

        ParseUser.logInInBackground(BuildConfig.DUMMY_USERNAME, BuildConfig.DUMMY_PASSWORD, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    UserModel.currentUser = user;
                    Log.d("TestActivity ***", "constructing light objects");

                    Random rng = new Random();
                    LightModel.today().addLightToHour(0, rng.nextInt());
                    String r1 = String.valueOf(rng.nextInt());
                    String r2 = String.valueOf(rng.nextInt());
                    LightModel.today().putTimeRange(r1, r2);
                    LightModel.today().save();
                    Log.d("PARSE PUT ***", "HOUR-LIGHT: " + LightModel.today().getTimeseries()[0]);
                    Log.d("PARSE PUT ***", "START-END " + r1 + ", " + r2);

                    Calendar startDay = Calendar.getInstance();
                    startDay.set(Calendar.DAY_OF_MONTH, startDay.get(Calendar.DAY_OF_MONTH) - 3);
                    Calendar endDay = Calendar.getInstance();

                    List<LightObject> lights = LightModel.getLightBetweenDays(TestActivity.this, startDay, endDay);
                    for (LightObject result: lights) {
                        Log.d("PARSE GET ***", "GOT ID " + result.getParseObject().getObjectId() + " WITH PARAMS: " + result.getDay().get(Calendar.DATE) + " " + result.getDay().get(Calendar.HOUR) + " " + + result.getDay().get(Calendar.MINUTE));
                    }
                } else {
                    Toast toast = Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }
}
