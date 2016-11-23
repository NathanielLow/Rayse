package com.rayse;

/**
 * Created by Nathaniel on 4/29/2016.
 */
public class WatchLightModel {

    private static WatchLightModel Instance = null;
    private int[] minutes = new int[24];
    private int goal = Integer.MAX_VALUE;

    private WatchLightModel() {
        for (int i = 0; i < minutes.length; i++) {
            minutes[i] = 0;
        }
    }

    public static WatchLightModel getInstance() {
        if (Instance == null) {
            Instance = new WatchLightModel();
        }
        return Instance;
    }

    public int[] getMinutes() {
        return minutes;
    }

    public int getGoal() {
        return goal;
    }

    public double getGoalPecent() {
        int goalMinutes = goal;
        double minuteSum = 0.0;
        for (int minute : minutes) {
            minuteSum += minute;
        }
        double percent = minuteSum / goalMinutes;
        if (percent > 1) {
            percent = 1;
        }
        return percent;
    }

    public void setMinutes(int index, int minute) {
        minutes[index] = minute;
    }

    public void setGoal(int goalMinutes) {
        goal = goalMinutes;
    }
}
