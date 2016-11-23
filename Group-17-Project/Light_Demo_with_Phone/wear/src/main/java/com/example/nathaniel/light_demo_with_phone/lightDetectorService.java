package com.example.nathaniel.light_demo_with_phone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class lightDetectorService extends Service {
    private SensorManager mSensorManager;
    private SensorListen listen;
    private Sensor light;
    private static boolean mSensingEnabled = false;
    public static final String BROADCAST = lightDetectorService.class.getName() + "LightBroadcast";
    public static final String LIGHT = "light";
    private boolean inSun = false;
    private Date startTime;
    private Date endTime;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
//        if (intent.getStringExtra("COMMAND").equals("stop")) {
//            Log.d("JBEIBERRRRRRRRR", "YOOOOOOOOOOOO");
//            onDestroy();
//        } else {
//            mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
//            listen = new SensorListen();
//            light = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//            mSensorManager.registerListener(listen, light, SensorManager.SENSOR_DELAY_NORMAL);
//        }
        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        listen = new SensorListen();
        light = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(listen, light, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(listen);
    }


    public class SensorListen implements SensorEventListener {

        private SensorEventLoggerTask mTask;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        private void sendBroadcastMessage(float luxValue) {
            if (luxValue != 0.0) {
                Intent intent = new Intent(BROADCAST);
                intent.putExtra(LIGHT, String.valueOf(luxValue));
                LocalBroadcastManager.getInstance(lightDetectorService.this).sendBroadcast(intent);
            }
        }


        @Override
        public void onSensorChanged(SensorEvent event) {
            //do something here
            Log.d("WE ON BOYSS", "onSensorChanged: light sensor");
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                mTask = (SensorEventLoggerTask) new SensorEventLoggerTask().execute(event);
            }
        }

        private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
            long diffInMillies = date2.getTime() - date1.getTime();
            return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }

        private class SensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {
            @Override
            protected Void doInBackground(SensorEvent... events) {
                float lxVal = events[0].values[0];
                // Do something with this sensor data.
                if (lxVal > 500 && !inSun) {
                    inSun = true;
                    startTime = new Date();
                }
                else if (inSun && lxVal < 500) {
                    endTime = new Date();
                    inSun = false;
                    long minutes = getDateDiff(startTime, endTime, TimeUnit.MINUTES);
                    Intent toPhone = new Intent(lightDetectorService.this, WatchToPhoneService.class);
                    toPhone.putExtra("LUX_VALUE", String.valueOf(lxVal));
                    toPhone.putExtra("MINUTES", String.valueOf(minutes));
                    startService(toPhone);
                    sendBroadcastMessage(lxVal);
                }
                mTask.cancel(true);
                return null;
            }
        }
    }
}
