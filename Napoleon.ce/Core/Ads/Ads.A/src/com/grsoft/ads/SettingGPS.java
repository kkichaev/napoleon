package com.grsoft.ads;

import com.grsoft.util.GlobalServiceContext;

public class SettingGPS extends Setting {
	@Override
	protected String[] getPrefs() {
		return new String[]{DISTANCE, FREQUENCE};
	}
	
	@Override
	protected int getSettingId() {
		return R.xml.setting_gps;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		((AdsService)GlobalServiceContext.service).gpsInit();
	}
}
