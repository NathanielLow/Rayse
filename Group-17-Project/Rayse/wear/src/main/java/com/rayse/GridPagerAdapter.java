package com.rayse;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Nathaniel on 4/16/2016.
 */
public class GridPagerAdapter extends FragmentGridPagerAdapter {

    private String[] images;
    private ArrayList<SimpleRow> mPages = new ArrayList<>();
    private Context context;

    public GridPagerAdapter(Context context, FragmentManager fm, String pics) {
        super(fm);
        this.context = context;
        images = pics.split("[!]+");
        initPages();
    }

    private void initPages() {
        SimpleRow row1 = new SimpleRow();
        row1.addPages(new Page(images[0]));
        SimpleRow row2 = new SimpleRow();
        row2.addPages(new Page(images[1]));
        mPages.add(row1);
        mPages.add(row2);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = (mPages.get(row)).getPages(col);
        Intent toPhone = new Intent(context, WatchToPhoneService.class);
        toPhone.putExtra("COMMAND", "requestData");
        context.startService(toPhone);
        Log.d("GridPager", "Starting Watch2Phone requestData");

        if (row == 0) {
            return WatchFragment.newInstance(page.image);
        }
        return WatchGraphFragment.newInstance(page.image);
    }

    @Override
    public int getRowCount() {
        return mPages.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return mPages.get(rowNum).size();
    }

    private static class Page {
        String image;

        public Page(String picture) {
            image = picture;
        }
    }

    public class SimpleRow {

        ArrayList<Page> mPagesRow = new ArrayList<>();

        public void addPages(Page page) {
            mPagesRow.add(page);
        }

        public Page getPages(int index) {
            return mPagesRow.get(index);
        }

        public int size(){
            return mPagesRow.size();
        }
    }
}
