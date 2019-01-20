package com.DriverAssistSystem.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.DriverAssistSystem.R;
import com.DriverAssistSystem.SoundAndMsg;
import com.DriverAssistSystem.TimerTest;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jinhong on 2017-07-21.
 */

public class Showing_TabFragment1 extends Fragment {
    private TextView drive_text, sleep_text, danger_text;
    private Button stopBtn, resetBtn, alertBtn, logBtn;
    private ImageView sleep_img, danger_img;
    private static String stringsLog[] = new String[100];
    private static int countLog = 0;
    public static int count_sleep = 0, count_danger = 0;
    public static int log_heart_avr = 0, heart_count=0;
    public static int log_breath_avr = 0;
    boolean stopFlag = true;
    private String heart_rate = "0", repiratory_rate = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.showingtab_fragment_1, container, false);
        TextView text = (TextView) view.findViewById(R.id.showingTitle1);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DXMob.otf");
        text.setTypeface(face);

        drive_text = (TextView) view.findViewById(R.id.drive_value);
        sleep_text = (TextView) view.findViewById(R.id.sleep_value);
        danger_text = (TextView) view.findViewById(R.id.danger_value);
        sleep_img = (ImageView) view.findViewById(R.id.sleep_image);
        danger_img = (ImageView) view.findViewById(R.id.danger_image);
        stopBtn = (Button) view.findViewById(R.id.stopBtn);
        resetBtn = (Button) view.findViewById(R.id.resetBtn);
        alertBtn = (Button) view.findViewById(R.id.alert);
        logBtn = (Button) view.findViewById(R.id.logBtn);

        final TimerTest timertest = TimerTest.getInstance();
        timertest.setTvTimer(drive_text);

        stopBtn.setOnClickListener(new Button.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (stopFlag == true) {
                                               timertest.stopTimer();
                                               stopFlag = false;
                                               stopBtn.setText("restart");
                                               long now = System.currentTimeMillis();
                                               Date date = new Date(now);
                                               SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                                               String getCurTime = sdf.format(date);
                                               if(countLog==98) countLog=0;
                                               stringsLog[countLog] = "0" + "," + getCurTime + "," + drive_text.getText().toString()
                                                       + "," + count_danger + "," + count_sleep + "," + log_heart_avr + "," + log_breath_avr + "\n";
                                               countLog++;
                                           } else {
                                               timertest.startTimer();
                                               stopFlag = true;
                                               stopBtn.setText("Stop");
                                           }
                                       }
                                   }
        );
        resetBtn.setOnClickListener(new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (stopFlag == true) {
                                                timertest.stopTimer();
                                                stopFlag = false;
                                                stopBtn.setText("start");
                                            }
                                            timertest.resetTimer();
                                        }
                                    }
        );
        alertBtn.setOnClickListener(new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String mNum=null, tel=null;
                                            if((mNum = new Setting_TabFragment2().getStrNumber()) == null) {
                                                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                                                ad.setMessage("!!!.\n**Alarm Ringing.");   // 내용 설정
                                                ad.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();     //닫기
                                                    }
                                                });
                                                ad.show();
                                            } else{
                                                tel = "tel:" + mNum;
                                                startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
                                            }
                                            count_danger++;

                                            long now = System.currentTimeMillis();
                                            Date date = new Date(now);
                                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                                            String getCurTime = sdf.format(date);
                                            if(countLog==98) countLog=0;
                                            stringsLog[countLog] = "1" + "," + getCurTime + "," + drive_text.getText().toString()
                                                    + "," + count_danger + "," + log_heart_avr + "," + log_breath_avr + ",1" + "\n";
                                            countLog++;
                                            stringsLog[countLog] = "0" + "," + getCurTime + "," + drive_text.getText().toString()
                                                    + "," + count_danger + "," + count_sleep + "," + log_heart_avr + "," + log_breath_avr + "\n";
                                            countLog++;
                                        }
                                    }
        );
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LogDialogActivity.class);

                intent.putExtra("CountLog", countLog);
                for (int i = 0; i < countLog; i++) {
                    String key = "Log" + (i + 1);
                    intent.putExtra(key, stringsLog[i]);
                }
                startActivityForResult(intent, 2);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                break;
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
    boolean oneCheck = false;
    boolean oneSleepCheck = false;
    long nowCheck = 0l;
    long playTime = 5000l;

    private final BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final SoundAndMsg s = new SoundAndMsg(getActivity());
            if (Constants.INTENTFILTER_DATA.equals(action)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String _msg = bundle.getString(Constants.INTENTEXTRA_RECEIVED_DATA);
                    String[] _data = null;
                    if (IoTUtility.is_danger_signal(_msg)) {
                        long now = System.currentTimeMillis();


                        if(!oneCheck){
                            nowCheck = now;
                            oneCheck = true;
                            danger_text.setText("DANGER");
                            danger_img.setImageResource(R.drawable.red_alarm);
                            count_danger++;
                            Date date = new Date(now);
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                            String getCurTime = sdf.format(date);

                            if(countLog==98) countLog=0;
                            stringsLog[countLog] = "1" + "," + getCurTime + "," + drive_text.getText().toString()
                                    + "," + count_danger + "," + log_heart_avr + "," + log_breath_avr + ",0" + "\n";
                            countLog++;
                            stringsLog[countLog] = "0" + "," + getCurTime + "," + drive_text.getText().toString()
                                    + "," + count_danger + "," + count_sleep + "," + log_heart_avr + "," + log_breath_avr + "\n";
                            countLog++;

                            if(new Setting_TabFragment2().getStrNumber() != null) {
                            s.sendMsg(new Setting_TabFragment2().getStrNumber().toString(), new Setting_TabFragment1().getUsrName());
                            s.sleep_alarm("3");
                            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                            ad.setMessage("Message sent.\n**Alarm Ringing.");   // 내용 설정
                            ad.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            ad.show();
                            final Handler mHandler = new Handler();
                            final Runnable mMyTask = new Runnable() {
                                @Override public void run() {
                                    danger_text.setText("SAFE");
                                    danger_img.setImageResource(R.drawable.alarm);
                                }
                            };
                            mHandler.postDelayed(mMyTask, 1000); // 1초후에 실행
                        } else {
                            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                            ad.setMessage("Register Emergency Contact.");   // 내용 설정
                            ad.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            ad.show();
                        }
                        }  if(oneCheck && (now - nowCheck)>playTime) {
                            oneCheck = false;
                        }

                    }
                    if (IoTUtility.is_get_status(_msg)) {
                        _data = IoTUtility.get_status(_msg);
                        if (_data != null) {
                            if (Integer.parseInt(_data[4]) != 0) {
                                long now = System.currentTimeMillis();

                                if(!oneSleepCheck){
                                    nowCheck = now;
                                    oneSleepCheck = true;
                                    count_sleep++;
                                    sleep_img.setImageResource(R.drawable.sleeping);
                                    Date date = new Date(now);
                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                                    String getCurTime = sdf.format(date);
                                    String sleep_log = _data[4];

                                    s.sleep_alarm(sleep_log);
                                    if(countLog==98) countLog=0;
                                    stringsLog[countLog] = "2" + "," + getCurTime + "," + drive_text.getText().toString()
                                            + "," + count_sleep + "," + log_heart_avr + "," + log_breath_avr + "," + sleep_log +  "\n";
                                    countLog++;
                                    stringsLog[countLog] = "0" + "," + getCurTime + "," + drive_text.getText().toString()
                                            + "," + count_danger + "," + count_sleep + "," + log_heart_avr + "," + log_breath_avr + "\n";
                                    countLog++;
                                }
                                if(oneSleepCheck && (now - nowCheck)>playTime) {
                                    oneSleepCheck = false;
                                }

                            } else{
                                sleep_img.setImageResource(R.drawable.wake);
                            }
                            if (Integer.parseInt(_data[4]) == 0) //졸음
                                sleep_text.setText("NORMAL");
                            else if (Integer.parseInt(_data[4]) == 1)
                                sleep_text.setText("1STEP\n(CAUTION)");
                            else if (Integer.parseInt(_data[4]) == 2)
                                sleep_text.setText("2STEP\n(DANGER)");
                            else
                                sleep_text.setText("ERROR");

                            if (Integer.parseInt(_data[5]) != 0)
                                danger_img.setImageResource(R.drawable.red_alarm);
                            else
                                danger_img.setImageResource(R.drawable.alarm);

                            if (Integer.parseInt(_data[5]) == 0) // 건강위헙도
                                danger_text.setText("SAFE");
                            else if(Integer.parseInt(_data[5]) == 1)
                                danger_text.setText("DANGER");
                            else
                                danger_text.setText("ERROR");

                            heart_rate = _data[2];
                            repiratory_rate = _data[3];
                            if(heart_count!=0){
                                heart_count++;
                                log_heart_avr = (log_heart_avr*(heart_count-1) + Integer.parseInt(heart_rate)) / heart_count;
                                log_breath_avr = (log_breath_avr*(heart_count-1) + Integer.parseInt(repiratory_rate)) / heart_count;
                            } else {
                                log_heart_avr = Integer.parseInt(heart_rate);
                                log_breath_avr = Integer.parseInt(repiratory_rate);
                                heart_count++;
                            }
                        }
                    }
                }
            }
        }
    };

}



