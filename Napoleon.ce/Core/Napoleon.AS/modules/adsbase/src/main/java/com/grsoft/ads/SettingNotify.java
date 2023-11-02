package com.grsoft.ads;

import android.content.SharedPreferences;



public class SettingNotify extends ServiceConnectPreference {
	public static final String NEWTASKRCV = "newtaskrcv";
	public static final String TASKMISSED = "taskmissed";
	public static final String TASKMISSEDTIME = "taskmissedtime";
	public static final String TASKNOTIFY = "taskmnotify";
	
	@Override
	protected int getPreferenceResource() { return R.xml.preference_notify; }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);
		
		if (key.equals(TASKMISSEDTIME) && service != null)
			service.restartTaskMissed();
	}
}
