package com.DriverAssistSystem.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.DriverAssistSystem.R;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;

/**
 * Created by Jinhong on 2017-08-23.
 */


public class LogDialogActivity extends Activity {

    String[] stringsLog;
    String[] stringsLog_Risk, stringsLog_Sleep, stringsLog_Nor;
    int countLog=0, curLog=0, countLogNor=0;
    int countRisk=0, countSleep=0;

    float firstX=0f, firstY=0f;
    float secondX=0f, secondY=0f;
    boolean touchflag = false;
    LinearLayout logbackgroung, log_risk_layout, log_sleep_layout;
    TextView current_time, log_driving_time, log_risk, log_sleep, log_heart_rate, log_repiratory_rate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.log_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        logbackgroung = (LinearLayout) findViewById(R.id.logbackground);
        log_risk_layout = (LinearLayout) findViewById(R.id.log_risk_layout);
        log_sleep_layout = (LinearLayout) findViewById(R.id.log_sleep_layout);
        current_time = (TextView) findViewById(R.id.current_time);
        log_driving_time = (TextView) findViewById(R.id.log_driving_time);
        log_risk = (TextView) findViewById(R.id.log_risk);
        log_sleep = (TextView) findViewById(R.id.log_sleep);
        log_heart_rate = (TextView) findViewById(R.id.log_heart_rate);
        log_repiratory_rate = (TextView) findViewById(R.id.log_repiratory_rate);

        Intent intent = getIntent();
        countLog = intent.getIntExtra("CountLog", 0);

        stringsLog = new String[countLog];
        stringsLog_Nor = new String[countLog];
        stringsLog_Risk = new String[countLog];
        stringsLog_Sleep = new String[countLog];

        for(int i=0; i<countLog; i++) {
            String key = "Log"+(i+1);
            stringsLog[i] = intent.getStringExtra(key);
            String result = stringsLog[i].split(",")[0];
            if(result.equals("0")){
                stringsLog_Nor[countLogNor] = new String(stringsLog[i]);
                countLogNor++;
            } else if(result.equals("1")){
                stringsLog_Risk[countRisk] = new String(stringsLog[i]);
                countRisk++;
            } else if(result.equals("2")){
                stringsLog_Sleep[countSleep] = new String(stringsLog[i]);
                countSleep++;
            }
        }


        if(countLog!=0){
            logbackgroung.setOnTouchListener(onTouchListener);
            setText();
        } else {
            current_time.setText("X");
            log_driving_time.setText("X");
            log_risk.setText("X");
            log_sleep.setText("X");
            log_heart_rate.setText("X");
            log_repiratory_rate.setText("X");
        }

        log_risk_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                goToRiskDialog();
                return false;
            }
        });
        log_sleep_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                goToSleepDialog();
                return false;
            }
        });
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    firstX = event.getX();
                    firstY = event.getY();
                    touchflag = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(touchflag){
                        secondX = event.getX();
                        secondY = event.getY();

                        float moveX = firstX - secondX;
                        if(moveX > 80f){
                            touchflag = !touchflag;
                            curLog++;
                            if(curLog==countLogNor){
                                curLog--;
                            } else
                                setText();
                        } else if(moveX < -80f){
                            touchflag = !touchflag;
                            curLog--;
                            if(curLog==-1){
                                curLog++;
                            } else
                                setText();
                        }
                    }
                    break;
            }
            return true;
        }
    };

    public void setText() {
        if(countLogNor!=0){
            String result[] = stringsLog_Nor[curLog].split(",");
            current_time.setText(result[1]);
            log_driving_time.setText(result[2]);
            log_risk.setText(result[3]);
            log_sleep.setText(result[4]);
            log_heart_rate.setText(result[5]);
            log_repiratory_rate.setText(result[6]);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 3:
                break;
            case 4:
                break;
        }
    }

    public void goToRiskDialog() {
        Intent intent = new Intent(getApplicationContext(), RiskDialogActivity.class);
        intent.putExtra("CountLog", countRisk);
        for(int i=0; i<countRisk; i++) {
            String key = "Log"+(i+1);
            intent.putExtra(key, stringsLog_Risk[i]);
        }
        startActivityForResult(intent, 3);
    }

    public void goToSleepDialog() {
        Intent intent = new Intent(getApplicationContext(), SleepDialogActivity.class);
        intent.putExtra("CountLog", countSleep);
        for(int i=0; i<countSleep; i++) {
            String key = "Log"+(i+1);
            intent.putExtra(key, stringsLog_Sleep[i]);
        }
        startActivityForResult(intent, 4);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        unregisterReceiver(mBroadCastReceiver);
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        registerReceiver(mBroadCastReceiver, makeIntentFilter());
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
                    if(IoTUtility.is_danger_signal(bundle.getString(Constants.INTENTEXTRA_RECEIVED_DATA))){
                    }
                }
            }
        }
    };
}