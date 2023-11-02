package com.grsoft.ads;

import android.content.SharedPreferences;



public class SettingSyncBkg extends ServiceConnectPreference {
	public static final String SYNCBKGSTEP = "syncbkgstep";
	
	
	@Override protected int getPreferenceResource() {	return R.xml.preference_syncbkg;}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);
		
		if (key.equals(SYNCBKGSTEP) && service != null)
			service.restartSyncBkg();
	}
}
