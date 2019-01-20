package com.DriverAssistSystem;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Namsohyeon on 2017. 8. 11..
 */

public class SoundAndMsg {
    Activity a;
    static boolean alarmFlag = false;
    public SoundAndMsg(Activity activity) {
        a = activity;
    }
    public void sendSMS(String phoneNumber, String message){
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        ActivityCompat.requestPermissions(a ,new String[]{Manifest.permission.SEND_SMS},1);
        // 문자 보내는 상태를 감지하는 PendingIntent
        PendingIntent sentPI = PendingIntent.getBroadcast(a.getApplicationContext(), 0, new Intent(SENT), 0);
        // 문자 받은 상태를 감지하는 PendingIntent
        PendingIntent deliveredPI = PendingIntent.getBroadcast(a.getApplicationContext(), 0, new Intent(DELIVERED), 0);
        // 문자 보내는 상태를 감지하는 BroadcastReceiver를 등록한다.
        a.registerReceiver(new BroadcastReceiver() {

            // 문자를 수신하면, 발생.
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(a.getApplicationContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(a.getApplicationContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(a.getApplicationContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(a.getApplicationContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(a.getApplicationContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
        // 문자를 받는 상태를 확인하는 BroadcastReceiver를 등록.
        a.registerReceiver(new BroadcastReceiver() {


            // 문자를 받게 되면, 불린다.
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(a.getApplicationContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(a.getApplicationContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        // SmsManager를 가져온다.
        SmsManager sms = SmsManager.getDefault();
        // sms를 보낸다.
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    };
boolean oneCheck = false;
    long playTime = 0l;
    long nowCheck = 0l;
    public void sleep_alarm(String sleep_degree) {
        MediaPlayer mediaPlayer;
       // boolean oneCheck;

        long now = System.currentTimeMillis();

        Log.d("Main", Long.toString(nowCheck));
        //Date date = new Date(now);
        //SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
        //String getCurTime = sdf.format(date);
        if(!oneCheck){
            nowCheck = now;
            oneCheck = true;
            if(sleep_degree.equals("1")) {
                playTime = 3000l;
                mediaPlayer = MediaPlayer.create(a.getApplicationContext(), R.raw.step1_sleep);
               // mediaPlayer = MediaPlayer.
                mediaPlayer.start();
            }

            if(sleep_degree.equals("2")) {
                playTime = 5000l;
                mediaPlayer = MediaPlayer.create(a.getApplicationContext(), R.raw.step2_sleep);
                mediaPlayer.start();
            }

            if(sleep_degree.equals("3")) {
                playTime = 7000l;
                mediaPlayer = MediaPlayer.create(a.getApplicationContext(), R.raw.step3_sleep);
                mediaPlayer.start();
            }
        }
        if(oneCheck && (now - nowCheck)>playTime) {
            oneCheck = false;
        }

    }
    public void sendMsg(String p, String name) {
            sendSMS(p, "[Watch Out 앱에서 전달드립니다.] " + name + " 님이 설정하신 자동메세지 입니다. 사고가 의심됩니다. 도와주세요.");
    }
}
