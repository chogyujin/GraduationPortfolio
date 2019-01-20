package com.DriverAssistSystem.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.DriverAssistSystem.R;
import com.DriverAssistSystem.TimerTest;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;
import com.DriverAssistSystem.tcp.TCPClient;

public class MainActivity extends AppCompatActivity {
    Button showingBtn, settingBtn, loginBtn, signUpBtn;
    TextView userId;
    ShimmerTextView tv;
    Shimmer shimmer = new Shimmer();
    Boolean loginFlag = false;
    TimerTest timertest;

    //private String mip = "172.30.1.50"; //coffee namu
    //private String mip = "192.168.0.15"; //coffee bay
    //private String mip = "169.254.69.148";
    //private String mip = "192.168.43.160";
    //private String mip = "114.71.38.231";
    private String mip = "192.168.43.179";
    private String mport = "11000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/beyond_the_mountains.ttf");
        tv.setTypeface(face);
        shimmer.start(tv);
        shimmer.setDuration(2000);

        showingBtn = (Button) findViewById(R.id.showingBtn);
        settingBtn = (Button) findViewById(R.id.settingBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        userId = (TextView) findViewById(R.id.userID);
        loginBtn.setTypeface(face);
        signUpBtn.setTypeface(face);

        showingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_popup(1);
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_popup(2);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!loginFlag) //false인경우에 loginActivity로 이동
                    open_popup(3);
                else{
                    loginFlag = !loginFlag; // true -> false 로바꾸기
                    loginBtn.setText("LOGIN");
                    userId.setText("Welcome!");
                    signUpBtn.setEnabled(true);
                    showingBtn.setEnabled(false);
                    settingBtn.setEnabled(false);
                    timertest.resetTimer();
                }
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_popup(4);
            }
        });

        TCPClient.getInstance().setContext(this.getApplicationContext());
        attempt_connect();
        userId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempt_disconnect();
                attempt_connect();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    signUpBtn.setEnabled(false);
                    showingBtn.setEnabled(true);
                    settingBtn.setEnabled(true);
                    userId.setText(data.getExtras().getString("ID"));
                    loginBtn.setText("LOGOUT");
                    loginFlag = !loginFlag; // false -> true로 바꾸기
                    if(loginFlag){
                        timertest = TimerTest.getInstance();
                        timertest.startTimer();
                    }
                }
                break;
            case 2:
                break;
        }
    }
    private final BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(Constants.INTENTFILTER_CONNECTION.equals(action))
            {
                Bundle bundle = intent.getExtras();
                if(bundle != null)
                {
                    if(bundle.getInt(Constants.INTENTEXTRA_CONNECT) == 1)
                    {
                        finish();
                    }
                    if(bundle.getInt(Constants.INTENTEXTRA_CONNECT) ==0)
                    {
                        TCPClient.getInstance().stopsclient();
                    }
                }
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(TCPClient.getInstance().is_connected()==true) {
            attempt_disconnect();
        }
    }
    public void attempt_connect() {
        TCPClient.getInstance().set_ip_address(mip);
        TCPClient.getInstance().set_port(Integer.parseInt(mport));
        TCPClient.getInstance().startclient();
    }
    public void attempt_disconnect() {
        TCPClient.getInstance().stopsclient();
    }
    private void open_popup(int type)
    {
        Intent intent;
        switch(type)
        {
            case 1 :
                intent = new Intent(this, ShowingActivity.class);
                startActivity(intent);
                break;
            case 2 :
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case 3 :
                intent = new Intent(this, SignInActivity.class);
                intent.putExtra(SignInActivity.EXTRAS_ENDLESS_MODE, false);
                startActivityForResult(intent, 1);
                break;
            case 4 :
                intent = new Intent(this, SignUpActivity.class);
                intent.putExtra(SignInActivity.EXTRAS_ENDLESS_MODE, false);
                startActivityForResult(intent, 2);
                break;
        }
    }
}