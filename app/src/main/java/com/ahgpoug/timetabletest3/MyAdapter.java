package com.ahgpoug.timetabletest3;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter {
    static Context ctxt = null;
    public MyAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt=ctxt;
    }

    @Override
    public int getCount() {
        return(7);
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }

    @Override
    public String getPageTitle(int position) {
        return(PageFragment.getTitle(ctxt, getDay(position)));
    }

    private String getDay(int position)
    {
        return GlobalVariables.days[position];
    }

    public static Context getContext() {
        return ctxt;
    }
}
