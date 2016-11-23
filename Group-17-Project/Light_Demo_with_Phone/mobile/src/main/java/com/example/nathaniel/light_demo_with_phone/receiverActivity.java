package com.example.nathaniel.light_demo_with_phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class receiverActivity extends AppCompatActivity {

    private TextView textView;
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        textView = (TextView) findViewById(R.id.receivedText);
        startButton = (Button) findViewById(R.id.start);
        stopButton = (Button) findViewById(R.id.stop);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWatch = new Intent(receiverActivity.this, PhoneToWatchService.class);
                toWatch.putExtra("COMMAND", "start");
                startService(toWatch);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWatch = new Intent(receiverActivity.this, PhoneToWatchService.class);
                toWatch.putExtra("COMMAND", "stop");
                startService(toWatch);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String data = intent.getStringExtra(PhoneListenerService.LUX);
                        String[] splitData = data.split("!");
                        String luxValue = splitData[0];
                        String minutes = splitData[1];
                        textView.setText("(" + luxValue + ", " + minutes + ")");
                    }
                }, new IntentFilter(PhoneListenerService.BROADCAST)
        );
    }
}
