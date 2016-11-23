package com.rayse;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class AchievementsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentActivity activity = (FragmentActivity) super.getActivity();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_achievements, container, false);
        ListView achievements = (ListView) layout.findViewById(R.id.achievements_list);

        int[] statuses = new int[15];
        for (int i = 0; i < 3; i++) {
            statuses[i] = R.drawable.check_mark;
        }
        for (int i = 3; i < 15; i++) {
            statuses[i] = R.drawable.logo;
        }
        int[] titles = new int[] {R.string.welcome_achievment, R.string.schedule_achievement, R.string.day_achievment,
            R.string.three_day_achievement, R.string.five_day_achievement, R.string.week_achievement, R.string.month_achievement,
            R.string.fifty_achievement, R.string.hundred_achievement, R.string.sunrise_achievement, R.string.sunset_achievement,
            R.string.time_achievement, R.string.no_sun_achievement, R.string.overachiever_achievement, R.string.achievement_achievement};
        int[] descriptions = new int[] {R.string.welcome_text, R.string.schedule_text, R.string.day_text, R.string.three_day_text,
            R.string.five_day_text, R.string.week_text, R.string.month_text, R.string.fifty_text, R.string.hundred_text, R.string.sunrise_text,
            R.string.sunset_text, R.string.time_text, R.string.no_sun_text, R.string.overachiever_text, R.string.achievement_text};

        final AchievementsListAdapter adapter = new AchievementsListAdapter(activity, statuses, titles, descriptions);
        achievements.setAdapter(adapter);
        return layout;
    }
}
