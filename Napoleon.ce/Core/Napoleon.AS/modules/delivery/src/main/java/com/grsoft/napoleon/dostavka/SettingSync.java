package com.grsoft.napoleon.dostavka;

import com.grsoft.napoleon.dostavka.MainService.LocalBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class SettingSync extends BasePreferenceFragment{
	private MainService mainsrv;
	boolean bound = false;

	@Override
	protected int getPreferenceResource() {	return R.xml.sync_pref; }
	
	@Override
	public void onStart() {
		super.onStart();
		
		Intent intent = new Intent(getActivity(), MainService.class);
		getActivity().bindService(intent, srvcon, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		if(bound){
			mainsrv.restartTimer();
			getActivity().unbindService(srvcon);
			bound = false;
		}
	}
	
	private ServiceConnection srvcon = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			 LocalBinder binder = (LocalBinder) service;
			 mainsrv = binder.getService();
	         bound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bound = false;
		}
    };

}
