package com.rayse;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class WatchNotificationService extends IntentService {

    public WatchNotificationService() {
        super("WatchNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int notificationId = 001;

        Intent actionIntent = new Intent(getApplicationContext(), PhoneToWatchService.class);
        actionIntent.putExtra("COMMAND", "activity");
        PendingIntent actionPendingIntent =
                PendingIntent.getService(getApplicationContext(), 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.logo,
                        getString(R.string.app_name), actionPendingIntent)
                        .build();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.blue_background))
                        .setContentTitle("rayse")
                        .setContentText("Do you feel like getting more sun right now?")
//                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000})
                        .setDefaults(Notification.DEFAULT_ALL)
//                                .addAction(R.drawable.logo, getString(R.string.app_name), actionPendingIntent);
                        .extend(new NotificationCompat.WearableExtender().addAction(action));
//                Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(500);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
