package com.grsoft.ads;

import android.content.SharedPreferences;


public class SettingGPS extends ServiceConnectPreference{
	public static final String GPSFREQ = "gpsfreq";
	public static final String GPSDIST = "gpsdist";
	public static final String FREQ_DEF = "5"; //секунд
	public static final String DIST_DEF = "100"; //метров
	
	@Override
	protected int getPreferenceResource() {	return R.xml.preference_gps; }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);
		
		if (key.equals(GPSFREQ) || key.equals(GPSDIST))
			service.restartGPS();
	}
}
