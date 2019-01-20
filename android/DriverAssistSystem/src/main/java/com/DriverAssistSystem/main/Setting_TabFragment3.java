package com.DriverAssistSystem.main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.DriverAssistSystem.R;
import com.DriverAssistSystem.TimerTest;
import com.DriverAssistSystem.TimerTest2;
import com.DriverAssistSystem.circularbutton.CircularProgressButton;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;
import com.DriverAssistSystem.tcp.TCPClient;


/**
 * Created by Jinhong on 2017-07-21.
 */

public class Setting_TabFragment3 extends Fragment {

    CircularProgressButton circularButton1;
    private TextView breathrate_text, breathrate_value, heartrate_text, heartrate_value;
    private ImageView breathrate_img, heartrate_img;
    private static String avr_heart, avr_breath;
    private TextView count_text;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.settingtab_fragment_3, container, false);

        TextView txt = (TextView)V.findViewById(R.id.settingTitle3);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DXMob.otf");
        txt.setTypeface(face);

        count_text = (TextView) V.findViewById(R.id.count_text);
        final TimerTest2 timertest2 = TimerTest2.getInstance();
        timertest2.setTvTimer(count_text);

        breathrate_text = (TextView) V.findViewById(R.id.breathrate_text);
        breathrate_value = (TextView) V.findViewById(R.id.breathrate_value);
        heartrate_text = (TextView) V.findViewById(R.id.heartrate_text);
        heartrate_value = (TextView) V.findViewById(R.id.heartrate_value);
        breathrate_img = (ImageView) V.findViewById(R.id.breathrate_image);
        heartrate_img = (ImageView) V.findViewById(R.id.heartrate_image);
        circularButton1 = (CircularProgressButton) V.findViewById(R.id.circularButton1);
        circularButton1.setIndeterminateProgressMode(true);
        circularButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circularButton1.getProgress() == 0) {
                    circularButton1.setProgress(50);
                    set_info_to_server();
                    timertest2.startTimer();
                } else if (circularButton1.getProgress() == 100) {
                    circularButton1.setProgress(0);
                    timertest2.resetTimer();
                }
            }
        });

        return V;
    }
    public String getAvrHeart() { return avr_heart; }
    public String getAvrBreath() { return avr_breath; }
    private void set_info_to_server()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_user_info());
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
                    if(IoTUtility.is_user_info(_msg)) {
                        String[] _data = IoTUtility.get_user_info(_msg);
                        if (_data != null) {
                            _data = IoTUtility.get_user_info(_msg);
                            if(_data !=null) {
                                circularButton1.setProgress(100);
                                TimerTest2.getInstance().stopTimer();
                                avr_heart = _data[0];
                                int breath = Integer.parseInt(_data[1]) * 3;
                                avr_breath = Integer.toString(breath);

                                breathrate_text.setText("Breath Rate");
                                heartrate_text.setText("Heart Rate");
                                breathrate_img.setImageResource(R.drawable.lungs);
                                heartrate_img.setImageResource(R.drawable.cardiogram);
                                breathrate_value.setText(avr_breath + "TIMES");
                                heartrate_value.setText(avr_heart + "BPM");
                            }
                        }}
                }
            }
        }
    };
}