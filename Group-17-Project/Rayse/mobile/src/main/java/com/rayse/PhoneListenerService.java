package com.rayse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class PhoneListenerService extends WearableListenerService {
    private static final String LIGHT = "/light";
    private static final String GOAL_REQ= "/requestData";
    public static final String BROADCAST = PhoneListenerService.class.getName() + "LightBroadcast";
    public static final String TIME = "time";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("PhoneListener", "got path " + messageEvent.getPath());
        if(messageEvent.getPath().equalsIgnoreCase(LIGHT)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            sendBroadcastMessage(value);
        } else if (messageEvent.getPath().equalsIgnoreCase(GOAL_REQ)) {
            Intent sendGoalIntent = new Intent(getApplicationContext(), PhoneToWatchService.class);
            sendGoalIntent.putExtra("COMMAND", "pushData");
            getApplicationContext().startService(sendGoalIntent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void sendBroadcastMessage(String time) {
        if (!(time.equals(""))) {
            Intent intent = new Intent(BROADCAST);
            intent.putExtra(TIME, time);
            LocalBroadcastManager.getInstance(PhoneListenerService.this).sendBroadcast(intent);
        }
    }
}
