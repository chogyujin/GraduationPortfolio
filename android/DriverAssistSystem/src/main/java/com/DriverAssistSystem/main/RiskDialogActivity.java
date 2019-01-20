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

public class RiskDialogActivity extends Activity {
    String[] stringsLog_Risk;
    String[] stringsShow;
    int countLog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.log_risk);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent intent = getIntent();
        countLog = intent.getIntExtra("CountLog", 0);
        stringsLog_Risk = new String[countLog];
        stringsShow = new String[countLog];
        for(int i=0; i<countLog; i++) {
            String key = "Log"+(i+1);
            stringsLog_Risk[i] = intent.getStringExtra(key);
            String result[] = stringsLog_Risk[i].split(",");
            for(int r=0; r<result.length; r++) {
                Log.d("Log" , r + " " +  result[r]);
            }

            String risk_text = null;

            if(result[6].equals("0\n")) {
                risk_text = "Risk Detect";
                Log.d("RiskDialog", "risk_text:" + risk_text);

            } else if(result[6].equals("1\n")) {
                risk_text = "Emergency -> Call";
                Log.d("RiskDialog", "risk_text:" + risk_text);

            }
            Log.d("RiskDialog", "" + result[6].equals("0\n"));

            Log.d("RiskDialog", "risk_text:" + risk_text);

            stringsShow[i] = "Risk" + result[3] + "\n" + risk_text + "\nCurTime: " + result[1] + "\nDriveTime: " + result[2] + "\nheart_avr: "
                    + result[4] + "\nbreath_avr: " + result[5] + "\n\n";
        }
        setListView();
    }
    private void setListView() {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, stringsShow);
        ListView listView = (ListView) findViewById(R.id.risk_list);
        listView.setAdapter(listAdapter);
    }
}
