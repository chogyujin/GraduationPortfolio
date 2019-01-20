package com.DriverAssistSystem.main;

import com.dd.processbutton.iml.ActionProcessButton;
import com.DriverAssistSystem.ProgressGenerator;
import com.DriverAssistSystem.R;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;
import com.DriverAssistSystem.tcp.TCPClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SignInActivity extends Activity implements ProgressGenerator.OnCompleteListener {

    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    private String id, password;
    private boolean success;
    static private String avr_heart;
    static private String avr_breath;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sign_in);

        TextView txt = (TextView)findViewById(R.id.loginTitle);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AritaSans.otf");
        txt.setTypeface(face);

        final EditText editEmail = (EditText) findViewById(R.id.editEmail);
        final EditText editPassword = (EditText) findViewById(R.id.editPassword);
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        } else {
            btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);
        }
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = editEmail.getText().toString();
                password = editPassword.getText().toString();
                success = loginSuccess(id, password);

               // success = true;
               // onComplete();

                if(success == false) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(SignInActivity.this);
                    ad.setMessage("You have entered the wrong Id or password.\nPlease check again.");   // 내용 설정
                    ad.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    ad.show();
                    onDestroy();
                } else {
                    if(TCPClient.getInstance().is_connected()==false){
                        AlertDialog.Builder ad = new AlertDialog.Builder(SignInActivity.this);
                        ad.setMessage("TCP is not connected. \nPlease check again.");   // 내용 설정
                        success = false;
                        ad.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        ad.show();
                        onDestroy();
                       // onCreate(savedInstanceState);
                    }
                    else{
                        set_info_to_server();
                        success = false;
                        progressGenerator.start(btnSignIn);
                        btnSignIn.setEnabled(false);
                        editEmail.setEnabled(false);
                        editPassword.setEnabled(false);
                    }
                }

            }
        });
    }
    public boolean loginSuccess(String id_input, String password_input) {
        if(id_input.equals("") || password_input.equals("")){
            return false;
        }
        return true;
    }
    @Override
    public void onComplete() {
        if(success) {
            Intent intent = new Intent();
            intent.putExtra("ID", id);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
    private void set_info_to_server()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_confirm_ID(id, password));
    }
    public String getAvrHeart() { return avr_heart; }
    public String getAvrBreath() { return avr_breath; }
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
                    if(IoTUtility.is_confirm_id(_msg)) {
                        _data = IoTUtility.get_confirm_id(_msg);
                        AlertDialog.Builder ad = new AlertDialog.Builder(SignInActivity.this);
                        if(_data[0].equals("1")) {
                            success = true;
                            avr_heart = _data[1];
                            avr_breath = _data[2];
                        }
                        else if(_data[0].equals("-2")) {
                            ad.setMessage("You have entered the wrong Password.\nPlease check again.");   // 내용 설정
                            success = false;
                            ad.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            ad.show();
                            onDestroy();
                            onCreate(bundle);
                        }
                        else if(_data[0].equals("-1")) {
                            ad.setMessage("You have entered the wrong ID.\nPlease check again.");   // 내용 설정
                            success = false;
                            ad.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            ad.show();
                            onDestroy();
                            onCreate(bundle);
                        }
                    }
                }
            }
        }
    };
}
