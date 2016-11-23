package com.rayse;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeekFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeekFragment newInstance(String param1, String param2) {
        WeekFragment fragment = new WeekFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WeekFragment() {
        // Required empty public constructor
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

        // CREATES TOP GRAPH
        // Begin by setting up the Column Data
        List<Column> day = new ArrayList<Column>();
        List<LightObject> lights = getData();
        for (int i=0; i< lights.size(); i++){
            List<SubcolumnValue> sValues = new ArrayList<SubcolumnValue>();
            float j;
            j = 0;
            j = j + sumLight(lights.get(i));
            sValues.add(new SubcolumnValue(j,Color.rgb(152,205,228)));
            day.add(new Column(sValues));
        }
        ColumnChartData dailyInfo = new ColumnChartData(day);

        // Then create the Goal Line
        Calendar now = Calendar.getInstance();
        int totalGoal = 0;
        for (int i = 0; i < lights.size(); i++) {
            int nowDay = now.get(Calendar.DAY_OF_WEEK);
            totalGoal += UserModel.getGoals().get(nowDay);
            nowDay--;
            now.set(Calendar.DAY_OF_WEEK, nowDay);
        }
        int goal = totalGoal / lights.size();
        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(-1, goal));
        values.add(new PointValue(lights.size(), goal));

        Line line = new Line(values).setColor(Color.rgb(242,242,242)).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData dataLine = new LineChartData();
        dataLine.setLines(lines);

        ComboLineColumnChartView LBC = (ComboLineColumnChartView) mainLayout.findViewById(R.id.combo);
        ComboLineColumnChartData finalData = new ComboLineColumnChartData(dailyInfo, dataLine);
        LBC.setComboLineColumnChartData(finalData);

        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        for (int i = 0; i < lights.size(); i++) {
            axisValues.add(new AxisValue(i).setLabel(lights.get(i).getDay().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())));
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
        for(int count = 0; count < lights.size(); count++){
            total += (int) sumLight(lights.get(count));
            totes.add(new PointValue(count, total));
        }
        Line filling = new Line(totes);
        filling.setColor(Color.rgb(255,179,0));
        filling.setFilled(true);

        List<Line> culmative = new ArrayList<Line>();
        culmative.add(filling);

        //Next create the Goal Line
        List<PointValue> goalValues = new ArrayList<PointValue>();
        goalValues.add(new PointValue(0, totalGoal));
        goalValues.add(new PointValue(lights.size() - 1, totalGoal));
        Line goalLine =  new Line(goalValues);
        goalLine.setColor(Color.rgb(242,242,242));
        culmative.add(goalLine);

        LineChartView LCV = (LineChartView) mainLayout.findViewById(R.id.total);
        LineChartData Data2 = new LineChartData(culmative);
        LCV.setLineChartData(Data2);

        Data2.setAxisXBottom(axisX);
        Data2.setAxisYLeft(axisY);

        return mainLayout;
    }

    public List<LightObject> getData(){
        Calendar dummyDay = Calendar.getInstance();
        Calendar day1 = new GregorianCalendar(dummyDay.get(Calendar.YEAR), dummyDay.get(Calendar.MONTH), dummyDay.get(Calendar.DAY_OF_MONTH)-7);
        List<LightObject> lightTime = LightModel.getLightBetweenDays(getActivity(), day1, dummyDay);
        return lightTime;
    }

    public int sumLight(LightObject lo){
        int total = 0;
        for(int i = 0; i < lo.getTimeseries().length; i++){
            total += lo.getTimeseries()[i];
        }
        return total;
    }

//    public int getGoal(){
//        return 600;
//    }
//
//    public int getActiveHours(){
//        return getEnd()-getStart();
//    }
//
//    public int getStart(){
//        return 0;
//    }
//
//    public int getEnd(){
//        return 7;
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
