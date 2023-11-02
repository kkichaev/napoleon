/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   04/05/2011   creating
 */
package com.grsoft.napoleon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.SettingActivity;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.ArrayList;
import java.util.List;

/***
 * Настройки параметров GPS
 * 
 * @author kki
 * 
 */
public class GpsSetting extends SettingActivity {

	public static final String TAG = "GpsSetting";
	private Controller controller;
	private boolean serviceBound = false;
	private NapoleonServiceW napoleonService;
	protected CheckBox cbSendDataInBackground;
	private Spinner spGPSValidInOrg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewID());
		init();
	}

	protected int getContentViewID(){ return R.layout.gps_setting; }
	
	protected void init() {
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();

		EditText edFrec = (EditText) findViewById(R.id.edFrec);
		edFrec.setText(Integer.toString(config.gpsFrequience
				/ Consts.ONE_SECOND));

		EditText edDist = (EditText) findViewById(R.id.edDist);
		edDist.setText(Integer.toString(config.gpsDistance));

		controller = new Controller(this);
		cbSendDataInBackground = (CheckBox) findViewById(R.id.cbSendDataInBackground);
		cbSendDataInBackground.setOnCheckedChangeListener(controller);
		cbSendDataInBackground.setChecked(config.dataSendInBackground);

		String[] send_interval = getResources().getStringArray(
				R.array.gps_send_interval);
		int pos = 0;
		int send_value = config.gpsSendInterval;;

		try {
			for (pos = 0; pos < send_interval.length; pos++)
				if (send_value == Integer.parseInt(send_interval[pos]))
					break;
		} catch (Exception e) {
		}

		Spinner spDataSendInterval = (Spinner) findViewById(R.id.spDataSendInterval);
		if(pos >= send_interval.length) {
			List<String> siv = new ArrayList<>();
			for (String s : send_interval) siv.add(s);
			siv.add(Integer.toString(send_value));
			spDataSendInterval.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, siv));
		}
		spDataSendInterval.setSelection(pos);
		spDataSendInterval.setOnItemSelectedListener(controller);

		spDataSendInterval.setEnabled(cbSendDataInBackground.isChecked());
		
		int wait_value = ((com.grsoft.napoleon.util.CfgNplW)config).waitGpsCoordOnRequest;
		try {
			for (pos = 0; pos < send_interval.length; pos++)
				if (wait_value == Integer.parseInt(send_interval[pos]))
					break;
		} catch (Exception e) {
		}
		
		Spinner spWaitGPSOnRecieve = (Spinner) findViewById(R.id.spWaitGPSOnRecieve);
		if(pos >= send_interval.length) {
			List<String> siv = new ArrayList<>();
			for (String s : send_interval) siv.add(s);
			siv.add(Integer.toString(wait_value));
			spWaitGPSOnRecieve.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, siv));
		}
		spWaitGPSOnRecieve.setSelection(pos);
		
		spGPSValidInOrg = (Spinner) findViewById(R.id.spGPSValidInOrg);

		if(spGPSValidInOrg != null){
			int gv = config.gps_valid_in_org / (Consts.SEC_PER_MIN * Consts.ONE_SECOND);
			
			try{
				String[] agv = getResources().getStringArray(R.array.gps_valid_in_org_entries);
				
				for(pos = 0; pos < agv.length; pos++)
					if(gv == Integer.parseInt(agv[pos]))
						break;
				if(pos >= agv.length) {
					List<String> vv = new ArrayList<>();
					for(String v : agv) vv.add(v);
					vv.add(Integer.toString(gv));
					spGPSValidInOrg.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, vv));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			spGPSValidInOrg.setSelection(pos, true);
		}
	}

	@Override
	public void save() {
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		EditText edFrec = (EditText) findViewById(R.id.edFrec);
		try{
			config.gpsFrequience = Integer.parseInt(edFrec.getText().toString())
				* Consts.ONE_SECOND;
		}catch(Exception e){
			config.gpsFrequience = Consts.ONE_SECOND * 60;
		}

		EditText edDist = (EditText) findViewById(R.id.edDist);
		config.gpsDistance = Integer.parseInt(edDist.getText().toString());

		try {
			Spinner spDataSendInterval = (Spinner) findViewById(R.id.spDataSendInterval);
			config.gpsSendInterval  = Integer
					.parseInt((String) spDataSendInterval.getSelectedItem());
		} catch (Exception e) {}

		CheckBox cbSendDataInBackground = (CheckBox) findViewById(R.id.cbSendDataInBackground);
		config.dataSendInBackground = cbSendDataInBackground.isChecked();
		
		try {
			Spinner spWaitGPSOnReceive = (Spinner) findViewById(R.id.spWaitGPSOnRecieve);
			((com.grsoft.napoleon.util.CfgNplW)config).waitGpsCoordOnRequest = Integer
					.parseInt((String) spWaitGPSOnReceive.getSelectedItem());
		} catch (Exception e) {}
		
		if(spGPSValidInOrg != null){
			String sel = spGPSValidInOrg.getSelectedItem().toString();
			int val = CfgNplW.DEF_VAL_FOR_TIME_GPS_IN_ORG;
	
			try{
				val = Integer.parseInt(sel) * Consts.SEC_PER_MIN * Consts.ONE_SECOND ;
			}catch(Exception e){
				e.printStackTrace();
			}
			
			config.gps_valid_in_org = val;
		}
		
		if (serviceBound)
			napoleonService.update();
		
		updateConfig();
		
		ConfigManager.save();
		
		if(NapoleonServiceW.isTracking()){
			GPSUtilNew.stop(this);
			GPSUtilNew.start(this);
		}
	}

	protected void updateConfig() {}

	@Override
	public void update() {
		init();
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			napoleonService = ((NapoleonServiceW.LocalBinder) service)
					.getService();
			serviceBound = true;
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = new Intent(this, Napoleon.serviceType);
		boolean bindResult = getApplicationContext().
			bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		Log.d(TAG, String.format("onStart: bindService %s", Boolean.toString(bindResult)));
		
	};

	@Override
	protected void onStop() {
		super.onStop();
		if (serviceBound) {
			getApplicationContext().unbindService(serviceConnection);
			serviceBound = false;
		}
	}

	@Override
	public int getName() {
		return R.string.gpsValue;
	}

	@Override
	public int getIcon() {
		return R.drawable.gpsno;
	}
	
	@Override
	public boolean isAdminSettings() {
		return true;
	}
}

class Controller implements OnCheckedChangeListener, OnItemSelectedListener {

	GpsSetting gpsSetting;
	Spinner spDataSendInterval;

	public Controller(GpsSetting gpsSetting) {
		this.gpsSetting = gpsSetting;
		spDataSendInterval = (Spinner) gpsSetting
				.findViewById(R.id.spDataSendInterval);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		spDataSendInterval.setEnabled(isChecked);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
