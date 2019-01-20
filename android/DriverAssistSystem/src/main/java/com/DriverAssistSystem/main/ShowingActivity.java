package com.DriverAssistSystem.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.DriverAssistSystem.R;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;
import com.DriverAssistSystem.tcp.TCPClient;

/**
 * Created by Jinhong on 2017-07-21.
 */

public class ShowingActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Handler mHandler;
    private GetInfoThread _info_thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout1);
        tabLayout.addTab(tabLayout.newTab().setText("Driving State"));
        tabLayout.addTab(tabLayout.newTab().setText("Driver State"));
        tabLayout.addTab(tabLayout.newTab().setText("Car State"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = (ViewPager) findViewById(R.id.pager1);
        ShowingTabPagerAdapter showingTabPagerAdapter = new ShowingTabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(showingTabPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        get_info_from_server();
        mHandler = new Handler();
        _info_thread = new GetInfoThread(true);
        _info_thread.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        _info_thread.stopThread();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENTFILTER_DATA);
        return intentFilter;
    }
    private void get_info_from_server() {
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_get_status());
    }
    class GetInfoThread extends Thread {
        private boolean isPlay = false;
        public GetInfoThread(boolean isPlay) {
            this.isPlay = isPlay;
        }
        public void stopThread() {
            isPlay = !isPlay;
        }
        @Override
        public void run() {
            super.run();
            while (isPlay) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        get_info_from_server();
                    }
                });
            }
        }
    }
}
