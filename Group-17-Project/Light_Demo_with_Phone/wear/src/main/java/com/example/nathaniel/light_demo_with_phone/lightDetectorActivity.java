package com.example.nathaniel.light_demo_with_phone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class lightDetectorActivity extends Activity {

    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_detector);

//        Intent start = new Intent(this, lightDetectorService.class);
//        startService(start);
        
        textView = (TextView) findViewById(R.id.text);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String luxValue = intent.getStringExtra(lightDetectorService.LIGHT);
                        textView.setText(luxValue);
                    }
                }, new IntentFilter(lightDetectorService.BROADCAST)
        );
    }
}
