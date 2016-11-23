package com.rayse;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nathaniel on 4/16/2016.
 */
public class WatchGraphFragment extends Fragment {

    private static final String ARG_IMAGE = "image";
    private String image;

    public WatchGraphFragment() {

    }

    public static WatchGraphFragment newInstance(String mImage) {
        WatchGraphFragment fragment = new WatchGraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE, mImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image = getArguments().getString(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.fragment_watch_graph, container, false);
        Date date = new Date();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date.getTime());
        String[] dateComponents = formattedDate.split("-");
        final int[] minutes = WatchLightModel.getInstance().getMinutes();
        final int hour = Integer.valueOf(dateComponents[3]);
        setBarWidths(v, minutes, hour);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        setBarWidths(v, minutes, hour);
                    }
                }, new IntentFilter(WatchListenerService.BROADCAST)
        );

        return v;
    }

    private void setBarWidths(RelativeLayout v, int[] minutes, int hour) {
        View barOne = (View) v.findViewById(R.id.bar_one);
        View barTwo = (View) v.findViewById(R.id.bar_two);
        View barThree = (View) v.findViewById(R.id.bar_three);
        ViewGroup.LayoutParams paramsOne = barOne.getLayoutParams();
        ViewGroup.LayoutParams paramsTwo = barTwo.getLayoutParams();
        ViewGroup.LayoutParams paramsThree = barThree.getLayoutParams();

        if (hour > 1) {
            paramsOne.width = minutes[hour - 2] * 3;
            RayseTextView time1 = (RayseTextView) v.findViewById(R.id.time1);
            RayseTextView minutes1 = (RayseTextView) v.findViewById(R.id.minutes1);
            time1.setText(formatHour(hour - 2));
            minutes1.setText(Integer.toString(minutes[hour - 2]) + " min");
        }
        if (hour > 0) {
            paramsTwo.width = minutes[hour - 1] * 3;
            RayseTextView time2 = (RayseTextView) v.findViewById(R.id.time2);
            RayseTextView minutes2 = (RayseTextView) v.findViewById(R.id.minutes2);
            time2.setText(formatHour(hour - 1));
            minutes2.setText(Integer.toString(minutes[hour - 1]) + " min");
        }

        paramsThree.width = minutes[hour] * 3;
        barOne.setLayoutParams(paramsOne);
        barTwo.setLayoutParams(paramsTwo);
        barThree.setLayoutParams(paramsThree);

        RayseTextView time3 = (RayseTextView) v.findViewById(R.id.time3);
        RayseTextView minutes3 = (RayseTextView) v.findViewById(R.id.minutes3);
        time3.setText(formatHour(hour));
        minutes3.setText(Integer.toString(minutes[hour]) + " min");
    }

    private String formatHour(int hour) {
        int hour12 = hour % 12;
        if (hour12 == 0) {
            hour12 = 12;
        }
        if (hour > 12) {
            return Integer.toString(hour12) + " pm";
        } else {
            return Integer.toString(hour12) + " am";
        }
    }

}
