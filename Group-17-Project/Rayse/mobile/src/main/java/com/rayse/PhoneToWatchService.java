package com.rayse;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;

/**
 * Created by Nathaniel on 4/17/2016.
 */
public class PhoneToWatchService extends Service {

    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
            }
            @Override
            public void onConnectionSuspended(int cause) {
            }
        }).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d("PhoneToWatch", "Got null extras");
            return START_STICKY;
        }
        String command = extras.getString("COMMAND");
        if (command.equals("start")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mApiClient.connect();
                    Log.d("PhoneToWatch", "**** SENDING START");
                    sendMessage("/startlight", "start");
                }
            }).start();
        } else if (command.equals("stop")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mApiClient.connect();
                    sendMessage("/stoplight", "stop");
                }
            }).start();
        } else if (command.equals("activity")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mApiClient.connect();
                    sendMessage("/startactivity", "activity");
                }
            }).start();
        } else if (command.equals("pushData")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mApiClient.connect();
                    Calendar now = Calendar.getInstance();
                    int today = now.get(Calendar.DAY_OF_WEEK);
                    String goalString = Integer.toString(UserModel.getGoals().get(today));
                    String timeseriesString = "";
                    for (int minutes: LightModel.today().getTimeseries()) {
                        timeseriesString += Integer.toString(minutes) + ",";
                    }
                    timeseriesString = timeseriesString.substring(0, timeseriesString.length() - 1);
                    sendMessage("/data", goalString + ";" + timeseriesString);
                    Log.d("PhoneToWatch", "sending goal");
                }
            }).start();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage(final String path, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }
}
