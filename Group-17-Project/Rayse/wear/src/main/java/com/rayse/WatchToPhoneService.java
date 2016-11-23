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

public class WatchToPhoneService extends Service {
    private GoogleApiClient mWatchApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mWatchApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
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
        mWatchApiClient.disconnect();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        final String command = extras.getString("COMMAND");
        if (command == null) {
            return START_STICKY;
        }
        if (command.equals("requestData")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mWatchApiClient.connect();
                    sendMessage("/requestData", "filler");
                    Log.d("Watch2Phone", "sending goal request");
                }
            }).start();
        } else if (command.equals("sendLight")) {
            final String startTime = extras.getString("STARTTIME");
            final String endTime = extras.getString("ENDTIME");
            final String minutes = extras.getString("MINUTES");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mWatchApiClient.connect();
                    sendMessage("/light", startTime + "!" + endTime + "!" +  minutes);
                }
            }).start();
        }
        return START_STICKY;
    }

    private void sendMessage(final String path, final String text ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mWatchApiClient).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mWatchApiClient, node.getId(), path, text.getBytes() ).await();
                    Log.d("Watch2Phone", "Thread sent message");
                }
            }
        }).start();
    }
}
