package com.rayse;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import java.nio.charset.StandardCharsets;

/**
 * Created by Nathaniel on 4/17/2016.
 */
public class WatchListenerService extends WearableListenerService {

    private static final String START_PATH = "/startlight";
    private static final String STOP_PATH = "/stoplight";
    private static final String ACTIVITY_PATH = "/startactivity";
    private static final String GOAL_PATH = "/data";
    public static final String BROADCAST = WatchListenerService.class.getName() + "GoalBroadcast";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        String data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        Intent intent = new Intent(this, LightDetectorService.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(messageEvent.getPath().equalsIgnoreCase(START_PATH)) {
            Log.d("T", "about to start watch LightDetectorService with DATA: " + data );
            startService(intent);
        }
        else if (messageEvent.getPath().equalsIgnoreCase(STOP_PATH)) {
            Log.d("T", "about to stop watch LightDetectorService with DATA: " + data );
            stopService(intent);
        }
        else if (messageEvent.getPath().equalsIgnoreCase(ACTIVITY_PATH)) {
            Log.d("T", "about to start WatchNowActivity with DATA: " + data);
            Intent activity = new Intent(this, WatchNowActivity.class);
            activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(activity);
        }
        else if (messageEvent.getPath().equalsIgnoreCase(GOAL_PATH)) {
            String[] pair = data.split(";");
            int goal = Integer.valueOf(pair[0]);
            WatchLightModel.getInstance().setGoal(goal);

            String[] stringTimeseries= pair[1].split(",");
            if (stringTimeseries.length != 24) {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid data received from phone", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            for (int i = 0; i < stringTimeseries.length; i++) {
                WatchLightModel.getInstance().getMinutes()[i] = Integer.valueOf(stringTimeseries[i]);
            }
            sendGoalBroadcast();
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void sendGoalBroadcast() {
        Log.d("WatchListener", "sending broadcast");
        Intent intent = new Intent(BROADCAST);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
