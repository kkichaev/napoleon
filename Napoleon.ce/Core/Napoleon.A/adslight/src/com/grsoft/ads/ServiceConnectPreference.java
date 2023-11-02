package com.grsoft.ads;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.grsoft.ads.AdsService.AdsServiceBinder;


public abstract class ServiceConnectPreference extends BasePreferenceFragment {
	protected AdsService service;
	private boolean bound = false;
	
	private ServiceConnection connector = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			service = ((AdsServiceBinder)binder).getService(); 
			bound = true;
		}
	};
	
	public void onStart() {
		super.onStart();
		Intent intent = new Intent(getActivity(), AdsService.MAIN_SERVICE);
		getActivity().bindService(intent, connector,  Context.BIND_AUTO_CREATE);
	};
	
	@Override
	public void onStop() {
		super.onStop();
	
		if(bound){
			getActivity().unbindService(connector);
			bound = false;
		}
	}
}
