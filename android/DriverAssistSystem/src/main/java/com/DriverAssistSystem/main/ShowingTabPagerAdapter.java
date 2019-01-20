package com.DriverAssistSystem.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Jinhong on 2017-07-21.
 */

public class ShowingTabPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;
    public ShowingTabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Showing_TabFragment1 tabFragment1 = new Showing_TabFragment1();
                return tabFragment1;
            case 1:
                Showing_TabFragment2 tabFragment2 = new Showing_TabFragment2();
                return tabFragment2;
            case 2:
                Showing_TabFragment3 tabFragment3 = new Showing_TabFragment3();
                return tabFragment3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
