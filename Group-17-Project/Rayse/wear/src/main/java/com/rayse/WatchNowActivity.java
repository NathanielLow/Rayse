package com.rayse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;

public class WatchNowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_now);
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new GridPagerAdapter(this, getFragmentManager(), "watch_now!watch_graph"));
    }
}
