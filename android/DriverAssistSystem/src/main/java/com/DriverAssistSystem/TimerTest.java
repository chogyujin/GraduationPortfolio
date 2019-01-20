package com.DriverAssistSystem;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jinhong on 2017-08-13.
 */

public class TimerTest {
    Timer timer;
    int timerCount;
    int hour, min, sec;
    Handler handler = new Handler();
    public TextView tvTimer;
    Activity a;
    private static final TimerTest ourInstance = new TimerTest();
    public static TimerTest getInstance() {
        return ourInstance;
    }
    private TimerTest() {}
    public void setTvTimer(TextView tvTimer) {
        this.tvTimer = tvTimer;
    }
    public void startTimer()
    {
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                timerCount++;
                update();
            }
        }, 0, 1000);
    }
    public void resetTimer() {
        timer.cancel();
        timerCount = 0;
        update();
    }
    public void stopTimer() {
        timer.cancel();
        update();
    }
    public void update()
    {
        Runnable updater = new Runnable()
        {
            public void run()
            {
                hour = timerCount / 3600;
                min = (timerCount - (hour * 3600)) / 60;
                sec = (timerCount - (hour * 3600 + min * 60));
                if(tvTimer!=null) {
                    if (hour > 0)
                        tvTimer.setText(String.valueOf(hour) + "h " + String.valueOf(min) + "m " + String.valueOf(sec) + "s");
                    else if( hour == 0 && min > 0)
                        tvTimer.setText(String.valueOf(min) + "m " + String.valueOf(sec) + "s");
                    else
                        tvTimer.setText(String.valueOf(sec) + "s");
                }
                driveTimeNotice(timerCount);
            }
        };
        handler.post(updater);
    }
    public void driveTimeNotice(int time) {
        MediaPlayer mediaPlayer;
        if(time == 3600) {
            mediaPlayer = MediaPlayer.create(a.getApplicationContext(), R.raw.hour);
            mediaPlayer.start();
        }
        else if(time == 7200) {
            mediaPlayer = MediaPlayer.create(a.getApplicationContext(), R.raw.hour2);
            mediaPlayer.start();
        }
    }
}
