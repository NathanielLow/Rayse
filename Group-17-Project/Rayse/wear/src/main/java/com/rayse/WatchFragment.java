package com.rayse;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.opengl.Visibility;
import android.os.Bundle;
//import android.app.Fragment;


import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Nathaniel on 4/16/2016.
 */
public class WatchFragment extends Fragment {

    private static final String ARG_IMAGE = "image";
    private String image;
    private final int EMULATOR_ADJUSTMENT = 0;
    float scale;

    public WatchFragment() {

    }

    public static WatchFragment newInstance(String mImage) {
        WatchFragment fragment = new WatchFragment();
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

        scale = getResources().getDisplayMetrics().density;
        Activity activity = getActivity();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_watch, container, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels - EMULATOR_ADJUSTMENT;
        int centerX = width / 2;
        int radius = (int) (centerX * 1/2.0);

        final TextView percentText = (TextView) layout.findViewById(R.id.now_percent);
        RelativeLayout.LayoutParams percentTextParams = (RelativeLayout.LayoutParams) percentText.getLayoutParams();
        percentTextParams.topMargin = centerX / 3;
        percentText.setLayoutParams(percentTextParams);

        final TextView labelText = (TextView) layout.findViewById(R.id.now_label);
        RelativeLayout.LayoutParams labelTextParams = (RelativeLayout.LayoutParams) labelText.getLayoutParams();
        labelTextParams.width = (int) (centerX * 3/4.0);
        labelText.setLayoutParams(labelTextParams);

        final SunProgressView progress = (SunProgressView) layout.findViewById(R.id.progress);
        progress.redraw(centerX, radius, radius);

        final SunProgressView.ProgressFillView progressFill = new SunProgressView.ProgressFillView(activity, progress, progress.sunPath, new Point(centerX, radius), progress.circleRadius);
        RelativeLayout.LayoutParams progressFillParams = new RelativeLayout.LayoutParams(progress.getLayoutParams());
        progressFillParams.setMargins(0, (int) (12 * scale), 0, 0);
        progressFill.setLayoutParams(progressFillParams);

        double percent = WatchLightModel.getInstance().getGoalPecent();
        progressFill.setPercent(percent);
        percentText.setText((int) (percent * 100) + "%");
        layout.addView(progressFill);
        // To get around Android's lack of z-index
        layout.removeView(percentText);
        layout.addView(percentText);
        layout.removeView(labelText);
        layout.addView(labelText);

        final EditText editPercent = (EditText) layout.findViewById(R.id.now_input_percent);
        editPercent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    double percent;
                    try {
                        percent = Double.valueOf(editPercent.getText().toString()) / 100;
                    } catch (NumberFormatException e) {
                        return true;
                    }
                    progressFill.setPercent(percent);
                    percentText.setText((int) (percent * 100) + "%");
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;
            }
        });
        editPercent.setVisibility(View.GONE);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double percent = WatchLightModel.getInstance().getGoalPecent();
                        Log.d("WatchFragment", "Got broadcast percent " + percent);
                        progressFill.setPercent(percent);
                        percentText.setText((int) (percent * 100) + "%");
                    }
                }, new IntentFilter(WatchListenerService.BROADCAST)
        );
        return layout;
    }

}
