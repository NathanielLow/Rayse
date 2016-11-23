package com.rayse;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LightDetectorService extends Service {
    private SensorManager mSensorManager;
    private SensorEventListener listen;
    private Sensor light;
    public static final String BROADCAST = LightDetectorService.class.getName() + "LightBroadcast";
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

        @Override
        public void onSensorChanged(SensorEvent event) {
            //do something here
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
                    int minutes = (int) getDateDiff(startTime, endTime, TimeUnit.MINUTES);
                    String startTimeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(startTime.getTime());
                    String endTimeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(endTime.getTime());

                    String[] startTimeComponents = startTimeStamp.split("-");
                    String[] endTimeComponents = endTimeStamp.split("-");
                    String startHour = startTimeComponents[3];
                    String endHour = endTimeComponents[3];
                    int intStartHour = Integer.parseInt(startHour);
                    int intEndHour = Integer.parseInt(endHour);

                    if (Integer.parseInt(startHour) != Integer.parseInt(endHour)) {
                        int laterMinutes = Integer.parseInt(endTimeComponents[4]);
                        WatchLightModel.getInstance().setMinutes(intEndHour, laterMinutes);
                        minutes -= laterMinutes;
                        for (int i = 0; i < intEndHour - intStartHour - 1; i++) {
                            WatchLightModel.getInstance().setMinutes(intEndHour - i - 1, 60);
                            minutes -= 60;
                        }
                        WatchLightModel.getInstance().setMinutes(intStartHour, minutes);
                    }
                    else {
                        WatchLightModel.getInstance().setMinutes(intEndHour, minutes);
                    }

                    Intent toPhone = new Intent(LightDetectorService.this, WatchToPhoneService.class);
                    toPhone.putExtra("COMMAND", "sendLight");
                    toPhone.putExtra("STARTTIME", startTimeStamp);
                    toPhone.putExtra("ENDTIME", endTimeStamp);
                    toPhone.putExtra("MINUTES", String.valueOf(minutes));
                    Log.d("LightDetector", "Sending light data to WatchToPhone");
                    startService(toPhone);
                }
                mTask.cancel(true);
                return null;
            }
        }
    }
}
