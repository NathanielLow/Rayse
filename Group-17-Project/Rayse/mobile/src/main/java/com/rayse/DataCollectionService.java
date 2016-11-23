package com.rayse;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;

public class DataCollectionService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("DataCollection", "Received light data from watch");
                        String data = intent.getStringExtra(PhoneListenerService.TIME);
                        String[] splitData = data.split("!");
                        String startTime = splitData[0];
                        String endTime = splitData[1];
                        LightModel.today().putTimeRange(startTime, endTime);

                        int min = Integer.parseInt(splitData[2]);
                        String[] startTimeComponents = startTime.split("-");
                        String[] endTimeComponents = endTime.split("-");
                        String startHour = startTimeComponents[3];
                        String endHour = endTimeComponents[3];
                        int intStartHour = Integer.parseInt(startHour);
                        int intEndHour = Integer.parseInt(endHour);

                        if (Integer.parseInt(startHour) != Integer.parseInt(endHour)) {
                            int laterMinutes = Integer.parseInt(endTimeComponents[4]);
                            LightModel.today().addLightToHour(intEndHour, laterMinutes);
                            min -= laterMinutes;
                            for (int i = 0; i < intEndHour - intStartHour - 1; i++) {
                                LightModel.today().addLightToHour(intEndHour - i - 1, 60);
                                min -= 60;
                            }
                            LightModel.today().addLightToHour(intStartHour, min);
                        }
                        else {
                            LightModel.today().addLightToHour(intEndHour, min);
                        }
                        LightModel.today().save();
                    }
                }, new IntentFilter(PhoneListenerService.BROADCAST)
        );
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
