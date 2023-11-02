package com.ksoft.ewatch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class Main extends Activity {
	public static final String PREFNAME = "prefname";
	public static final String SMSACTIVE = "smsactive";
	public static final String PHONE = "phone";
	public static final String POWERONTEXT = "powerontext";
	public static final String POWEROFFTEXT = "powerofftext";
	
	private CheckBox cbSMS;
	private EditText edPhone;
	private EditText edPowerOn;
	private EditText edPowerOff;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main);
		
		cbSMS = (CheckBox) findViewById(R.id.cbSMS);
		edPhone = (EditText) findViewById(R.id.edPhone);
		edPowerOn = (EditText) findViewById(R.id.edPowerOn);
		edPowerOff = (EditText) findViewById(R.id.edPowerOff);
		
		cbSMS.setOnCheckedChangeListener(onChecked());
	}

	private OnCheckedChangeListener onChecked() {
		return new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences pref =  getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
				
				Editor ed = pref.edit();
				ed.putBoolean(SMSACTIVE, isChecked);
				ed.commit();
			}
		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences pref =  getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
		edPhone.setText(pref.getString(PHONE, ""));
		cbSMS.setChecked(pref.getBoolean(SMSACTIVE, false));
		edPowerOn.setText(pref.getString(POWERONTEXT, getString(R.string.power_on)));
		edPowerOff.setText(pref.getString(POWEROFFTEXT, getString(R.string.power_off)));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		SharedPreferences pref =  getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putString(PHONE, edPhone.getText().toString().trim());
		ed.putString(POWERONTEXT, edPowerOn.getText().toString().trim());
		ed.putString(POWEROFFTEXT, edPowerOff.getText().toString().trim());
		
		ed.commit();
	}
}
