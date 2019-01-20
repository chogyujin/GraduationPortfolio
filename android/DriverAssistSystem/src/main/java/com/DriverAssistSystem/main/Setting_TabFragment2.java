package com.DriverAssistSystem.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.DriverAssistSystem.R;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;
import com.DriverAssistSystem.tcp.TCPClient;

/**
 * Created by Jinhong on 2017-07-21.
 */

public class Setting_TabFragment2 extends Fragment {
    private Spinner spinner2;
    private Button confirmBtn;
    private EditText editText_Name, editText_Number;
    private static String strName, strNumber, strRelation;
    private static int intRelation=0;
    private static boolean firstSave=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.settingtab_fragment_2, container, false);

        TextView txt = (TextView)V.findViewById(R.id.settingTitle2);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DXMob.otf");
        txt.setTypeface(face);

        spinner2 = (Spinner) V.findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter =ArrayAdapter.createFromResource(getActivity(), R.array.spinner2, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        editText_Name = (EditText) V.findViewById(R.id.settingTab2_Name);
        editText_Number = (EditText) V.findViewById(R.id.settingTab2_Number);
        confirmBtn = (Button) V.findViewById(R.id.settingTab2_ConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstSave = false;
                strName = editText_Name.getText().toString();
                strNumber = editText_Number.getText().toString();
                strRelation = spinner2.getSelectedItem().toString();
                intRelation = spinner2.getSelectedItemPosition();

                if(confirmSuccess()) {
                    set_info_to_server();
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                    ad.setMessage("Saved Succesfully .......\n");   // 내용 설정
                    ad.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    ad.show();
                } else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                    ad.setMessage("You have empty .......\nPlease check again.");   // 내용 설정
                    ad.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    ad.show();
                }
            }
        });
        if(!firstSave){
            editText_Name.setText(strName);
            editText_Number.setText(strNumber);
            spinner2.setSelection(intRelation);
        }
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_get_phone_num());
        return V;
    }
    private boolean confirmSuccess() {
        if(strName.equals("") || strNumber.equals("")){
            return false;
        }
        return true;
    }
    private void set_info_to_server()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_set_phonenum(strName, strNumber, intRelation));
    }
    public String getStrNumber(){ return strNumber; }
    public String getStrName(){ return strName; }
    public String getStrRelation(){ return strRelation; }
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
                    String[] _data = null;
                    if(IoTUtility.is_get_phone_num(_msg)) {
                        _data = IoTUtility.get_phone_num(_msg);
                        if(_data.length != 0) {
                            strName = _data[0];
                            strNumber = _data[1];
                            intRelation = Integer.parseInt(_data[2]);
                            editText_Name.setText(strName);
                            editText_Number.setText(strNumber);
                            spinner2.setSelection(intRelation);
                        }
                    }
                }
            }
        }
    };
}