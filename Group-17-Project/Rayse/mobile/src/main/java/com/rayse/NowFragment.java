package com.rayse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class NowFragment extends Fragment {
    private final int EMULATOR_ADJUSTMENT = 0;
    float scale;
    boolean showPercentLeft = false;
    double percent;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scale = getResources().getDisplayMetrics().density;
        FragmentActivity activity = (FragmentActivity) super.getActivity();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_now, container, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels - EMULATOR_ADJUSTMENT;
        int centerX = width / 2;
        int radius = (int) (centerX * 3/4.0);

        final TextView percentText = (TextView) layout.findViewById(R.id.now_percent);
        RelativeLayout.LayoutParams percentTextParams = (RelativeLayout.LayoutParams) percentText.getLayoutParams();
        percentTextParams.topMargin = centerX / 2;
        percentText.setLayoutParams(percentTextParams);

        final TextView labelText = (TextView) layout.findViewById(R.id.now_label);
        RelativeLayout.LayoutParams labelTextParams = (RelativeLayout.LayoutParams) labelText.getLayoutParams();
        labelTextParams.width = (int) (centerX * 3/4.0);
        labelText.setLayoutParams(labelTextParams);

        final SunProgressView progress = (SunProgressView) layout.findViewById(R.id.progress);
        progress.redraw(centerX, radius, radius);

        final SunProgressView.ProgressFillView progressFill = new SunProgressView.ProgressFillView(activity, progress, progress.sunPath, new Point(centerX, radius), progress.circleRadius);
        layout.addView(progressFill);
        // To get around Android's lack of z-index
        layout.removeView(percentText);
        layout.addView(percentText);
        layout.removeView(labelText);
        layout.addView(labelText);

        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_WEEK);
        int lowerBound = UserModel.getStarts().get(today);
        int upperBound = UserModel.getEnds().get(today);
        int endHour = Math.min(now.get(Calendar.HOUR_OF_DAY) + 1, upperBound);
        int startHour = Math.max(endHour - 5, lowerBound);
        int activeHours = endHour - startHour;
        if (endHour - startHour <= 0) {
            activeHours = 1;
            labelText.setText("No light data yet");
        }
        int goalMinutes = UserModel.getGoals().get(today);
        double minuteSum = 0.0;
        for (int minutes: LightModel.today().getTimeseries()) {
            minuteSum += minutes;
        }
        percent = minuteSum / goalMinutes;
        if (percent > 1) {
            percent = 1;
        }
        progressFill.setPercent(percent);
        percentText.setText((int) (percent * 100) + "%");

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

        final Button toggleButton = (Button) layout.findViewById(R.id.toggle_direction);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPercentLeft = !showPercentLeft;
                if (showPercentLeft) {
                    percentText.setText((int) ((1 - percent) * 100) + "%");
                    labelText.setText("left to achieve daily goal");
                } else {
                    percentText.setText((int) (percent * 100) + "%");
                    labelText.setText("of daily goal achieved");
                }
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        int goalMinutes = UserModel.getGoals().get(today);
                        double minuteSum = 0.0;
                        for (int minutes: LightModel.today().getTimeseries()) {
                            minuteSum += minutes;
                        }
                        percent = minuteSum / goalMinutes;
                        if (percent > 1) {
                            percent = 1;
                        }
                        Log.d("NowFragment", "Got broadcast percent " + percent);
                        progressFill.setPercent(percent);
                        percentText.setText((int) (percent * 100) + "%");
                    }
                }, new IntentFilter(SettingsFragment.BROADCAST)
        );
        // Comment this line to bring back the debug
        editPercent.setVisibility(View.GONE);

        // Begin graph logic
        List<Column> day = new ArrayList<Column>();
        ArrayList Datas = getData(startHour, endHour);
        for (int i=0; i<Datas.size(); i++){
            List<SubcolumnValue> sValues = new ArrayList<SubcolumnValue>();
            float j;
            j = 0;
            j = j + (int)Datas.get(i);
            sValues.add(new SubcolumnValue(j, Color.rgb(152, 205, 228)));
            day.add(new Column(sValues));
        }
        ColumnChartData dailyInfo = new ColumnChartData(day);

        //Then create the Goal Line

        int goal = goalMinutes / activeHours;
        int lineY = goal;
        Integer maxMinutes = 0;
        if (Datas.size() > 0) {
            maxMinutes = (Integer) Collections.max(Datas);
        }
        if (goal > maxMinutes) {
            lineY = maxMinutes;
        }
        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(-1, lineY));
        values.add(new PointValue(Datas.size(), lineY));

        Line line = new Line(values).setColor(Color.rgb(242,242,242)).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData dataLine = new LineChartData();
        dataLine.setLines(lines);

        ComboLineColumnChartView LBC = (ComboLineColumnChartView) layout.findViewById(R.id.graph_now);
        ComboLineColumnChartData finalData = new ComboLineColumnChartData(dailyInfo, dataLine);
        LBC.setComboLineColumnChartData(finalData);


        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (float i = 0; i < Datas.size(); i++) {
            // I'am translating float to minutes because I don't have data in minutes, if You store some time data
            // you may skip translation.
            if(startHour + i < 12) {
                axisValues.add(new AxisValue(i).setLabel("" + Integer.toString(
                        (int) (startHour + i)) + "am"));
            }else if(startHour + i == 12){
                axisValues.add(new AxisValue(i).setLabel("noon"));
            }else{
                axisValues.add(new AxisValue(i).setLabel("" + Integer.toString(
                        (int) (startHour + i - 12)) + "pm"));
            }
        }
        Axis axisX = new Axis(axisValues);
        axisX.setName("Time");
        finalData.setAxisXBottom(axisX);

        Axis axisY = new Axis();
        axisY.setName("Minutes in Sun");
        finalData.setAxisYLeft(axisY);


        return layout;
    }

    //CREATES TOP GRAPH
    //Begin by setting up the Column Data
    public ArrayList getData(int startHour, int endHour) {
        int[] lightTime= LightModel.today().getTimeseries();
        ArrayList Data = new ArrayList();
        for(int i=0; i<24; i++){
            Data.add(lightTime[i]);
        }
        ArrayList SubData = new ArrayList();
        for(int x = startHour; x < endHour; x++){
            SubData.add(Data.get(x));
        }
        return SubData;
    }

//    public int getGoal(){
//        return 120;
//    }

//    public int getActiveHours(){
//        return getEnd()-getStart();
//    }

//    public int getStart(){
//        int cur =  getEnd()- 3;
//        if(cur==-3){
//            return 0;
//        }else if(cur == -2){
//            return 1;
//        }else if(cur == -1){
//            return 2;
//        }else{
//            return cur;
//        }
//    }

//    public int getEnd(){
//        Calendar day = Calendar.getInstance();
//        return 21;
//    }

//    public int getTrueActive(){
//        return 12;
//    }
}
