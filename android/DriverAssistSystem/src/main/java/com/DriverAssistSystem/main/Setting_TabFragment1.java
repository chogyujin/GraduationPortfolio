package com.DriverAssistSystem.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.DriverAssistSystem.R;
import com.DriverAssistSystem.tcp.Constants;
import com.DriverAssistSystem.tcp.IoTUtility;
import com.DriverAssistSystem.tcp.TCPClient;

/**
 * Created by Jinhong on 2017-07-21.
 */

public class Setting_TabFragment1 extends Fragment {
    private Spinner spinner;
    private Button confirmBtn;
    private EditText editText_Name, editText_Age;
    private RadioGroup radioGroup_Sex;
    private RadioButton radioButton_Male, radioButton_Female;
    private static String strName, strAge, strSex, strDisease;
    private static int intSex=0, intDisease=0;
    private static boolean firstShowing=true, firstSave=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.settingtab_fragment_1, container, false);

        TextView txt = (TextView)V.findViewById(R.id.settingTitle1);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DXMob.otf");
        txt.setTypeface(face);

        spinner = (Spinner) V.findViewById(R.id.spinner1);
        confirmBtn = (Button) V.findViewById(R.id.settingTab1_ConfirmBtn);
        editText_Name = (EditText) V.findViewById(R.id.settingTab1_Name);
        editText_Age = (EditText) V.findViewById(R.id.settingTab1_Age);
        radioGroup_Sex = (RadioGroup) V.findViewById(R.id.settingTab1_Sex);
        radioButton_Male = (RadioButton) V.findViewById(R.id.radioButton_Male);
        radioButton_Female = (RadioButton) V.findViewById(R.id.radioButton_Female);

        ArrayAdapter<CharSequence> adapter =ArrayAdapter.createFromResource(getActivity(), R.array.spinner1, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        radioGroup_Sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioButton_Male) {
                    strSex = radioButton_Male.getText().toString();
                    intSex = 0;
                } else if (checkedId == R.id.radioButton_Female) {
                    strSex = radioButton_Female.getText().toString();
                    intSex = 1;
                }
            }

        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstSave = false;
                strName = editText_Name.getText().toString();
                strAge = editText_Age.getText().toString();
                intDisease = spinner.getSelectedItemPosition();
                strDisease = spinner.getSelectedItem().toString();

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
        if(firstShowing){
            firstShowing = !firstShowing;
            strSex = radioButton_Male.getText().toString();
            intSex = 0;
            radioButton_Male.setChecked(true);
        }
        if(!firstSave){
            editText_Name.setText(strName);
            editText_Age.setText(strAge);
            spinner.setSelection(intDisease);
            switch (intSex) {
                case 0:
                    radioButton_Male.setChecked(true);
                    break;
                case 1:
                    radioButton_Female.setChecked(true);
                    break;
                default:
                    break;
            }
        }
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_get_health());
        return V;
    }
    private boolean confirmSuccess() {
        if(strName.equals("") || strAge.equals("")){
            return false;
        }
        return true;
    }
    public String getUsrName() {
        return strName;
    }
    public int getIntDisease() { return intDisease; }
    public String getAge() { return strAge; }
    private void set_info_to_server()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TCPClient.getInstance().sendMessage(IoTUtility.mkcommand_set_health(strName, strAge, intSex, intDisease));
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
                    String[] _data = null;
                    if(IoTUtility.is_get_health(_msg)) {
                        _data = IoTUtility.get_health(_msg);
                        if(_data.length != 0) {
                            strName = _data[0];
                            strAge = _data[1];
                            intSex = Integer.parseInt(_data[2]);
                            intDisease = Integer.parseInt(_data[3]);
                            editText_Name.setText(strName);
                            editText_Age.setText(strAge);
                            spinner.setSelection(intDisease);
                            strDisease = spinner.getSelectedItem().toString();
                            switch (intSex) {
                                case 0:
                                    radioButton_Male.setChecked(true);
                                    break;
                                case 1:
                                    radioButton_Female.setChecked(true);
                                    break;
                                default:
                                    break;
                            }
                            strSex = radioButton_Male.getText().toString();
                        }
                    }
                }
            }
        }
    };
}