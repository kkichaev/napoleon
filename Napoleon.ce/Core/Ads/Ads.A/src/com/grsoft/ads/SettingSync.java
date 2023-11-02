package com.grsoft.ads;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.GlobalServiceContext;
import com.grsoft.ads.utils.ConfigReader;

public class SettingSync extends Setting {
	@Override
	protected int getSettingId() {
		return R.xml.setting_sync;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Preference interval = findPreference(INTERVAL);
		interval.setEnabled(((ConfigReader)ConfigManager.getConfig()).isDataSendInBackground());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Preference background = findPreference(DATA_SEND_IN_BACKGOUND);
		background.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Preference interval = findPreference(INTERVAL);
				interval.setEnabled((Boolean) newValue);
				return true;
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		((AdsService)GlobalServiceContext.service).update();
	}
}
