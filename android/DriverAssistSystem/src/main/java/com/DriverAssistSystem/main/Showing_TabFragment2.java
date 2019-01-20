package com.DriverAssistSystem.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.DriverAssistSystem.R;
import com.DriverAssistSystem.SnakeView;
import com.DriverAssistSystem.SoundAndMsg;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Jinhong on 2017-07-21.
 */

public class Showing_TabFragment2 extends Fragment {

    private TextView text;
    private SnakeView snakeView;
    private boolean stop = false;

    TextView breathText, breathDegree;
    ImageView greenView, yellowView, redView;
    float tempValue;
    float heart_min=50f , heart_max = 100f;
    int breath_min, breath_max;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.showingtab_fragment_2, container, false);

        TextView txt = (TextView) view.findViewById(R.id.showingTitle2);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DXMob.otf");

        txt.setTypeface(face);
        getActivity().getWindow().setBackgroundDrawable(null);

        snakeView = (SnakeView) view.findViewById(R.id.snake2);
        text = (TextView) view.findViewById(R.id.text);
        breathText = (TextView) view.findViewById(R.id.breathCnt);
        breathDegree = (TextView) view.findViewById(R.id.breath_degree);
        greenView = (ImageView) view.findViewById(R.id.green);
        yellowView  = (ImageView) view.findViewById(R.id.yellow);
        redView = (ImageView) view.findViewById(R.id.red);

        int disease = new Setting_TabFragment1().getIntDisease();
        int age = 0;
        if(new Setting_TabFragment1().getAge() != null) {
            age = Integer.parseInt(new Setting_TabFragment1().getAge());
        }
        float avr_heart = 75f;

        if(new SignInActivity().getAvrHeart() != null) {
            avr_heart = Float.parseFloat(new SignInActivity().getAvrHeart());
        }
        if(new Setting_TabFragment3().getAvrHeart() != null) {
            avr_heart = Float.parseFloat(new Setting_TabFragment3().getAvrHeart());
        }

        int avr_breath = 15;
        if(new SignInActivity().getAvrBreath() != null) {
            avr_breath = Integer.parseInt(new SignInActivity().getAvrBreath());
            avr_breath *= 3;
        }
        if(new Setting_TabFragment3().getAvrBreath() != null) {
            avr_breath = Integer.parseInt(new Setting_TabFragment3().getAvrBreath());
            avr_breath *= 3;
        }
        switch (disease) { // 기준
            case 0:  // 정상
                breath_min = avr_breath - 4;
                breath_max = avr_breath + 4;
                heart_min = avr_heart - 25f;
                if(age >= 65)
                    heart_max = 80f;
                else
                    heart_max = avr_heart + 25f;
                break;
            case 1: // 심근경색 - 심박수 20 높이기
                avr_heart += 18f;
                heart_min = avr_heart - 18f;
                heart_max = avr_heart + 18f;
                break;
            case 2: // 고혈압 - 심박수 10 높이기
                avr_heart += 10f;
                heart_min = avr_heart - 20f;
                heart_max = avr_heart + 20f;
                break;
            case 3:// 저혈압 - 심박수 10 낮추기
                avr_heart -= 10f;
                heart_min = avr_heart - 20f;
                heart_max = avr_heart + 20f;
                break;
            case 4: // 폐렴 //정상 : 12-20 // 폐렴 : 25회 이상  + 5씩 높이기.
                avr_breath = 21;
                breath_min = avr_breath -4;
                breath_max = avr_breath + 4;
                break;
            default:
                break;
        }
        snakeView.setMaxMinRange(heart_min, heart_max);
        generateValue();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        stop = false;
    }
    @Override
    public void onStop() {
        super.onStop();
        stop = true;
    }
    private void generateValue() {
        snakeView.addValue(tempValue);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!stop) {
                    generateValue();
                }
            }
        }, 1000);
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
    long nowCheck = 0l;
    long playTime = 5000l;

    private final BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            SoundAndMsg s = new SoundAndMsg(getActivity());
            if (Constants.INTENTFILTER_DATA.equals(action)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String _msg = bundle.getString(Constants.INTENTEXTRA_RECEIVED_DATA);
                    if (IoTUtility.is_danger_signal(_msg)) {
                       /*
                        danger_text.setText("DANGER");
                        danger_img.setImageResource(R.drawable.red_alarm);
                        count_danger++;
                        long now = System.currentTimeMillis();
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
                        */
                        long now = System.currentTimeMillis();

                        if(!oneCheck){
                            nowCheck = now;
                            oneCheck = true;

                            Log.d("Main", Long.toString(nowCheck));

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
                        }}  if(oneCheck && (now - nowCheck)>playTime) {
                            oneCheck = false;
                        }

                    }

                    if (IoTUtility.is_get_status(_msg)) {
                        String[] _data = IoTUtility.get_status(_msg);
                        if (_data != null) {
                            String heart_rate = _data[2];
                            text.setText(heart_rate);
                            tempValue = Float.valueOf(_data[2]);
                            int breath = Integer.parseInt(_data[3]) * 3;
                            String repiratory_rate = Integer.toString(breath);

                            breathText.setText(repiratory_rate);

                            Log.d("Showing2", breath_max + " " + breath_min + " " + breath);

                            if(breath > breath_max) //호흡
                                breathDegree.setText("HIGH");
                            else if(breath >= breath_min && breath <= breath_max)
                                breathDegree.setText("NORMAL");
                            else
                                breathDegree.setText("LOW");

                            if(breath > breath_max + 5 || breath < breath_min - 5) { //호흡 색 // 빨강 - 위험
                                greenView.setImageResource(R.drawable.black);
                                yellowView.setImageResource(R.drawable.black);
                                redView.setImageResource(R.drawable.red);
                            }
                            else if(breath > breath_max && breath <= breath_max + 5) { // 주의
                                greenView.setImageResource(R.drawable.black);
                                yellowView.setImageResource(R.drawable.yellow);
                                redView.setImageResource(R.drawable.black);
                            }
                            else if(breath < breath_min && breath >= breath_min - 5) { // 주의
                                greenView.setImageResource(R.drawable.black);
                                yellowView.setImageResource(R.drawable.yellow);
                                redView.setImageResource(R.drawable.black);
                            }
                            else {
                                greenView.setImageResource(R.drawable.green);
                                yellowView.setImageResource(R.drawable.black);
                                redView.setImageResource(R.drawable.black);
                            }
                        }
                    }
                }
            }
        }
    };
}
