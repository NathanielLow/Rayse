package com.example.nathaniel.light_demo_with_phone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WatchListenerService extends WearableListenerService {
    private static final String START_LIGHT = "/startlight";
    private static final String STOP_LIGHT = "/stoplight";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        String decision = new String(messageEvent.getData(), StandardCharsets.UTF_8);

        Intent intent = new Intent(this, lightDetectorService.class );
        intent.putExtra("COMMAND", decision);

        if(messageEvent.getPath().equalsIgnoreCase(START_LIGHT)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("T", "about to start watch lightDetectorService with DATA: " + decision);
            startService(intent);
        }
        else if (messageEvent.getPath().equalsIgnoreCase(STOP_LIGHT)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("T", "about to stop watch lightDetectorService with DATA: " + decision);
            stopService(intent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}
