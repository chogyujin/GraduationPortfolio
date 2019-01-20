package com.DriverAssistSystem.main;

import com.dd.processbutton.iml.ActionProcessButton;
import com.DriverAssistSystem.ProgressGenerator;
import com.DriverAssistSystem.R;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;
import com.DriverAssistSystem.tcp.TCPClient;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity implements ProgressGenerator.OnCompleteListener {
    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    EditText editJoinEmail, editJoinPassword, confirmPassword;
    Button btnSignUp;
    private String strJoinEmail, strJoinPassword, strConfirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_join);

        TextView txt = (TextView)findViewById(R.id.signUpTitle);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AritaSans.otf");
        txt.setTypeface(face);

        editJoinEmail = (EditText) findViewById(R.id.editJoinEmail);
        editJoinPassword = (EditText) findViewById(R.id.editJoinPassword);
        confirmPassword = (EditText) findViewById(R.id.passwordConfirm);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton btnSignUp = (ActionProcessButton) findViewById(R.id.btnSignUp);
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            btnSignUp.setMode(ActionProcessButton.Mode.ENDLESS);
        } else {
            btnSignUp.setMode(ActionProcessButton.Mode.PROGRESS);
        }
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strJoinEmail = editJoinEmail.getText().toString();
                strJoinPassword = editJoinPassword.getText().toString();
                strConfirmPassword = confirmPassword.getText().toString();
                int s = signUpSucess();
                AlertDialog.Builder ad = new AlertDialog.Builder(SignUpActivity.this);
                if(s == 2){
                    ad.setMessage("Passwords do not match.\nPlease check again.");   // 내용 설정
                    ad.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    ad.show();
                    onDestroy();
                } else if(s == 3){
                    ad.setMessage("You have not entered an ID or password.\nPlease check again.");   // 내용 설정
                    ad.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    ad.show();
                    onDestroy();
                } else {
                    if(TCPClient.getInstance().is_connected()==false){
                        ad.setMessage("TCP is not connected. \nPlease check again.");   // 내용 설정
                        ad.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        ad.show();
                        onDestroy();
                    }
                    else {
                        set_info_to_server();
                        progressGenerator.start(btnSignUp);
                        btnSignUp.setEnabled(false);
                        editJoinEmail.setEnabled(false);
                        editJoinPassword.setEnabled(false);
                        confirmPassword.setEnabled(false);
                    }
                }
            }
        });
    }
    public int signUpSucess() {
        if(strJoinEmail.equals("") || strJoinPassword.equals("") || strConfirmPassword.equals("")){
            return 3;
        }
        else if(!strJoinPassword.equals(strConfirmPassword)) {
            return 2;
        }
        else {
            return 0;
        }
    }
    @Override
    public void onComplete() {
        finish();
    }
    private void set_info_to_server()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_signup_ID(strJoinEmail, strJoinPassword));
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
                    String _msg = bundle.getString(Constants.INTENTEXTRA_RECEIVED_DATA);
                    String[] _data = null;
                    if(IoTUtility.is_signup_id(_msg)) {
                        _data = IoTUtility.get_signup_id(_msg);
                       AlertDialog.Builder ad = new AlertDialog.Builder(SignUpActivity.this);
                        if(_data[0].equals("1")) {
                            ad.setMessage(editJoinEmail.getText().toString()+"님 가입되었습니다.");   // 내용 설정
                            ad.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                    onComplete();
                                }
                            });
                            ad.show();
                        }
                        else if(_data[1].equals("0")) {
                        }
                    }
                }
            }
        }
    };

}
