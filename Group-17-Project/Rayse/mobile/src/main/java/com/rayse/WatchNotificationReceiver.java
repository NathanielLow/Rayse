package com.rayse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WatchNotificationReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
//    public static final String ACTION = "com.rayse.WatchNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent broadcast = new Intent(context, WatchNotificationService.class);
        context.startService(broadcast);
    }
}
