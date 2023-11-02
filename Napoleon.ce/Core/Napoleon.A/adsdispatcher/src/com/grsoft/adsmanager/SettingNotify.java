package com.grsoft.adsmanager;

import android.content.SharedPreferences;



public class SettingNotify extends ServiceConnectPreference {
	public static final String NEWTASKRCV = "newtaskrcv";
	public static final String TASKMISSED = "taskmissed";
	public static final String TASKMISSEDTIME = "taskmissedtime";
	
	@Override
	protected int getPreferenceResource() { return R.xml.preference_notify; }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);
		
		
	}
}
