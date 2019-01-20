package com.DriverAssistSystem.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.DriverAssistSystem.R;

/**
 * Created by Jinhong on 2017-09-10.
 */

public class SleepDialogActivity extends Activity {
    String[] stringsLog_Sleep;
    String[] stringsShow;
    int countLog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.log_sleep);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent intent = getIntent();
        countLog = intent.getIntExtra("CountLog", 0);
        stringsLog_Sleep = new String[countLog];
        stringsShow = new String[countLog];
        for(int i=0; i<countLog; i++) {
            String key = "Log"+(i+1);
            stringsLog_Sleep[i] = intent.getStringExtra(key);
            String result[] = stringsLog_Sleep[i].split(",");
            // stringsLog[countLog] = "2" + "," + getCurTime + "," + drive_text.getText().toString()
            //+ "," + count_sleep + "," + log_heart_avr + "," + log_breath_avr + "," + _data[4] +  "\n";
/*
* D/Log: 0 2
10-19 19:34:04.252 25731-25731/com.tabtest1 D/Log: 1 07:33:39
10-19 19:34:04.252 25731-25731/com.tabtest1 D/Log: 2 1m 36s
10-19 19:34:04.252 25731-25731/com.tabtest1 D/Log: 3 1
10-19 19:34:04.252 25731-25731/com.tabtest1 D/Log: 4 62
10-19 19:34:04.252 25731-25731/com.tabtest1 D/Log: 5 1
10-19 19:34:04.252 25731-25731/com.tabtest1 D/Log: 6 2
*/
            for(int r=0; r<result.length; r++) {
                Log.d("Log" , r + " " +  result[r]);
            }
            String sleep_text = null;
            if (result[6].equals("1\n"))/* Caused by: java.lang.NumberFormatException: For input string: "2"*/
                sleep_text = "1STEP";
            else if (result[6].equals("2\n"))
                sleep_text = "2STEP";
            stringsShow[i] = "Sleep" + result[3] + "\n" + sleep_text +  "\nCurTime: " + result[1] + "\nDriveTime: " + result[2] + "\nheart_avr: "
                    + result[4] + "" +
                    "" +
                    "\nrepiratory: " + result[5] + "\n\n";
        }
        setListView();
    }
    private void setListView() {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, stringsShow);
        ListView listView = (ListView) findViewById(R.id.sleep_list);
        listView.setAdapter(listAdapter);
    }
}