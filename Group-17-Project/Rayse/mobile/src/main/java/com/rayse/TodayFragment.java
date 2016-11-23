package com.rayse;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {TodayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int startHour;
    private int endHour;
    private int activeHours;
    private int goalMinutes;

//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Make this where we create fragments instead of using the newInstance method above
    public TodayFragment() {
        // Required empty public constructor
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        startHour = UserModel.getStarts().get(today);
        Calendar now = Calendar.getInstance();
        endHour = now.get(Calendar.HOUR_OF_DAY) + 1;
        activeHours = endHour - startHour;
        if (endHour - startHour <= 0) {
            activeHours = 1;
        }
        goalMinutes = UserModel.getGoals().get(today);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout mainLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_today, container, false);

        //CREATES TOP GRAPH
        //Begin by setting up the Column Data
        List<Column> day = new ArrayList<Column>();
        ArrayList Datas = getData();
        for (int i = 0;  i < Datas.size(); i++){
            List<SubcolumnValue> sValues = new ArrayList<SubcolumnValue>();
            float j;
            j = 0;
            j = j + (int)Datas.get(i);
            sValues.add(new SubcolumnValue(j,Color.rgb(152,205,228)));
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

        ComboLineColumnChartView LBC = (ComboLineColumnChartView) mainLayout.findViewById(R.id.combo);
        ComboLineColumnChartData finalData = new ComboLineColumnChartData(dailyInfo, dataLine);
        LBC.setComboLineColumnChartData(finalData);

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (float i = 0; i < Datas.size(); i++) {
            if (startHour + i == 0 ) {
                axisValues.add(new AxisValue(i).setLabel("12 am"));
            } else if(startHour + i < 12) {
                axisValues.add(new AxisValue(i).setLabel("" + Integer.toString(
                        (int) (startHour + i)) + " am"));
            } else if(startHour + i == 12){
                axisValues.add(new AxisValue(i).setLabel("Noon"));
            } else{
                axisValues.add(new AxisValue(i).setLabel("" + Integer.toString(
                        (int) (startHour + i - 12)) + " pm"));
            }
        }
        Axis axisX = new Axis(axisValues);
        axisX.setName("Time");
        finalData.setAxisXBottom(axisX);

        Axis axisY = new Axis();
        axisY.setName("Minutes in Sun");
        finalData.setAxisYLeft(axisY);

        //CREATE BOTTOM GRAPH
            //Created filled in chart
        List<PointValue> totes = new ArrayList<PointValue>();
        int total = 0;
        for(int count = 0; count<Datas.size(); count++){
            total += (int)Datas.get(count);
            totes.add(new PointValue(count, total));
        }
        Line filling = new Line(totes);
        filling.setColor(Color.rgb(255,179,0));
        filling.setFilled(true);

        List<Line> cumulative= new ArrayList<Line>();
        cumulative.add(filling);

        //Next create the Goal Line
        List<PointValue> goalValues = new ArrayList<PointValue>();
        goalValues.add(new PointValue(0, goalMinutes));
        goalValues.add(new PointValue(activeHours - 1, goalMinutes));
        Line goalLine =  new Line(goalValues);
        goalLine.setColor(Color.rgb(242, 242, 242));
        cumulative.add(goalLine);

        LineChartView LCV = (LineChartView) mainLayout.findViewById(R.id.total);
        LineChartData Data2 = new LineChartData(cumulative);
        LCV.setLineChartData(Data2);

        Data2.setAxisXBottom(axisX);
        Data2.setAxisYLeft(axisY);

        return mainLayout;
    }

    public ArrayList getData(){
        int[] lightTime= LightModel.today().getTimeseries();
        try {
            Log.d("TodayFragment", "" + new JSONArray(lightTime));
        } catch (JSONException e) {

        }
        ArrayList Data = new ArrayList();
        for(int i=0; i < 24; i++){
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
//
//    public int getActiveHours(){
//        return getEnd()-getStart();
//    }
//
//    public int getStart(){
//        return 8;
//    }
//
//    public int getEnd(){
//        return 16;
//    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }
}
