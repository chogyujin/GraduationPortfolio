package com.DriverAssistSystem.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.DriverAssistSystem.CircularProgressBar;
import com.DriverAssistSystem.R;
import com.DriverAssistSystem.clicknumberpicker.ClickNumberPickerListener;
import com.DriverAssistSystem.clicknumberpicker.PickerClickType;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;

import com.DriverAssistSystem.clicknumberpicker.ClickNumberPickerView;
import com.DriverAssistSystem.tcp.TCPClient;

/**
 * Created by Jinhong on 2017-07-21.
 */

public class Showing_TabFragment3 extends Fragment {
    public static String temp,hum;
    CircularProgressBar c1, c2, c3, c4;
    ClickNumberPickerView picker1, picker2;
    Switch aSwitch, aSwitch2;
    static float temp_usr=18.0f, hum_usr=45.0f;
    static boolean auto_check=false, fan_check=false;
    boolean fan = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.showingtab_fragment_3,container,false);
        TextView txt = (TextView)view.findViewById(R.id.showingTitle3);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DXMob.otf");
        txt.setTypeface(face);

        ShowingActivity s = new ShowingActivity();
        c1 = (CircularProgressBar) view.findViewById(R.id.circularprogressbar1);
        c1.setProgress(25);
        c1.setTitle("25℃");
        c2 = (CircularProgressBar) view.findViewById(R.id.circularprogressbar2);
        c2.mProgressColorPaint.setColor(getResources().getColor(R.color.holo_orange_light));
        c2.mTitlePaint.setColor(getResources().getColor(R.color.holo_orange_light));
        c3 = (CircularProgressBar) view.findViewById(R.id.circularprogressbar3);
        c3.setProgress(55);
        c3.setTitle("55%");
        c4 = (CircularProgressBar) view.findViewById(R.id.circularprogressbar4);
        c4.mProgressColorPaint.setColor(getResources().getColor(R.color.holo_orange_light));
        c4.mTitlePaint.setColor(getResources().getColor(R.color.holo_orange_light));

        picker1 = (ClickNumberPickerView)view.findViewById(R.id.temp_picker);
        picker2 = (ClickNumberPickerView)view.findViewById(R.id.hum_picker);
        picker1.setUnable(true);
        picker2.setUnable(true);
        picker1.setPickerValue(temp_usr);
        picker2.setPickerValue(hum_usr);

        aSwitch = (Switch) view.findViewById(R.id.switch1);
        aSwitch2 = (Switch) view.findViewById(R.id.switch2);
        aSwitch2.setEnabled(true);
        aSwitch.setChecked(auto_check);
        aSwitch2.setChecked(fan_check);

        final Handler mHandler = new Handler();
        final Runnable mMyTask = new Runnable() {
            @Override public void run() {
                c1.setTitle(temp+"℃");
            }
        };
        final Runnable mMyTask2 = new Runnable() {
            @Override public void run() {
                c3.setTitle(hum+"%");
            }
        };
        picker1.setClickNumberPickerListener(new ClickNumberPickerListener() {
            @Override
            public void onValueChange(float previousValue, float currentValue, PickerClickType pickerClickType) {
                c1.setTitle(Integer.toString((int)currentValue) + "℃");
                c2.setProgress((int)currentValue);
                temp_usr = currentValue;
                mHandler.postDelayed(mMyTask, 2000); // 2초후에 실행
            }
        });
        picker2.setClickNumberPickerListener(new ClickNumberPickerListener() {
            @Override
            public void onValueChange(float previousValue, float currentValue, PickerClickType pickerClickType) {
                c3.setTitle(Integer.toString((int)currentValue) + "%");
                c4.setProgress((int)currentValue);
                hum_usr = currentValue;
                mHandler.postDelayed(mMyTask2, 2000); // 2초후에 실행
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                auto_check = isChecked;
                if (isChecked == true){//auto
                    c2.setProgress((int)temp_usr);
                    c4.setProgress((int)hum_usr);
                    fan=false;
                    picker1.setUnable(false);
                    picker2.setUnable(false);
                    fan_check=false;
                    aSwitch2.setChecked(fan_check);
                    enable_fan(false);
                    aSwitch2.setEnabled(false);
                } else { //수동
                    c2.setProgress(0);
                    c4.setProgress(0);
                    picker1.setUnable(true);
                    picker2.setUnable(true);
                    enable_fan(false);
                    aSwitch2.setEnabled(true);
                }
            }
        });
        aSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fan_check = isChecked;
                if (isChecked) { // Fan ON
                    enable_fan(true);
                }
                else {
                    enable_fan(false);
                }
            }
        });
        return view;
    }
    public void enable_fan(boolean flag){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_set_fan(flag));
    }
    public void auto_enable_fan() {
        if((Float.parseFloat(temp) > temp_usr && Float.parseFloat(hum) > hum_usr) && auto_check && !fan) {
            enable_fan(true);
            fan = !fan;
        } else if((Float.parseFloat(temp) <= temp_usr && Float.parseFloat(hum) <= hum_usr) && auto_check && fan){
            enable_fan(false);
            fan = !fan;
        }
    }
    @Override
    public synchronized void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadCastReceiver);
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadCastReceiver, makeIntentFilter());
    }
    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENTFILTER_DATA);
        return intentFilter;
    }
    private final BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(Constants.INTENTFILTER_DATA.equals(action))
            {
                Bundle bundle = intent.getExtras();
                if(bundle != null)
                {
                    String _msg = bundle.getString(Constants.INTENTEXTRA_RECEIVED_DATA);
                    String[] _data = null;
                    if(IoTUtility.is_get_status(_msg)){
                        _data = IoTUtility.get_status(_msg);
                        if(_data != null)
                        {
                            temp = _data[0];
                            hum = _data[1];
                            c1.setTitle(temp+"℃");
                            c3.setTitle(hum+"%");
                            if(auto_check)
                                auto_enable_fan();
                        }
                    }
                }
            }
        }
    };
}