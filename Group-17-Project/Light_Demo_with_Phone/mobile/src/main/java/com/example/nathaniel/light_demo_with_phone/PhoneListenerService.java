package com.example.nathaniel.light_demo_with_phone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class PhoneListenerService extends WearableListenerService {
    private static final String LIGHT = "/light";
    public static final String BROADCAST = PhoneListenerService.class.getName() + "LightBroadcast";
    public static final String LUX = "light";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if(messageEvent.getPath().equalsIgnoreCase(LIGHT)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            sendBroadcastMessage(value);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void sendBroadcastMessage(String luxValue) {
        if (!(luxValue.equals(""))) {
            Intent intent = new Intent(BROADCAST);
            intent.putExtra(LUX, luxValue);
            LocalBroadcastManager.getInstance(PhoneListenerService.this).sendBroadcast(intent);
        }
    }
}
