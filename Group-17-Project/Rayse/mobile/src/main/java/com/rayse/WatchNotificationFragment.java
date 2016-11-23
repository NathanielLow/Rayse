package com.rayse;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.LinearLayout;


/**
 * Created by Nathaniel on 4/16/2016.
 */
public class WatchNotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentActivity activity = (FragmentActivity) super.getActivity();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_watch_notification, container, false);
        Button wearButton = (Button) layout.findViewById(R.id.wearButton);
        wearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 001;

                Intent actionIntent = new Intent(activity, PhoneToWatchService.class);
                actionIntent.putExtra("COMMAND", "activity");
                PendingIntent actionPendingIntent =
                        PendingIntent.getService(activity, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action action =
                        new NotificationCompat.Action.Builder(R.drawable.logo,
                                getString(R.string.app_name), actionPendingIntent)
                                .build();

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(activity)
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
                NotificationManagerCompat.from(activity);

                notificationManager.notify(notificationId, notificationBuilder.build());

                Intent startLightDetectorIntent = new Intent(activity, PhoneToWatchService.class);
                startLightDetectorIntent.putExtra("COMMAND", "start");
                activity.startService(startLightDetectorIntent);
            }
        });
        return layout;
    }
}
