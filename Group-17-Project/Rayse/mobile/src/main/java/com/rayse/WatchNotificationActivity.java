package com.rayse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


/**
 * Created by Nathaniel on 4/16/2016.
 */
public class WatchNotificationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_watch);
        Button wearButton = (Button)findViewById(R.id.wearButton);
        wearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 001;
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(WatchNotificationActivity.this)
                                .setSmallIcon(R.drawable.watch_more_sun_notification)
                                .setContentTitle("Title")
                                .setContentText("Android Wear Notification");

                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(WatchNotificationActivity.this);

                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        });
    }
}
