package com.grsoft.ads;

import com.grsoft.ads.utils.ConfigReader;
import com.grsoft.napoleon.util.ConfigManager;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Setting extends PreferenceActivity {
	public static final String SHARED_PREFERENCES_NAME = "AdsSetting";
	public static final String LOGIN = "login";
	public static final String PASSW = "passw";
	public static final String SERV_ADR_1 = "serv_adr_1";
	public static final String SERV_ADR_2 = "serv_adr_2";
	public static final String PORT = "port"; 
	public static final String DISTANCE= "distance";
	public static final String FREQUENCE= "frequence";
	public static final String CLEAR = "clear";
	public static final String DATA_SEND_IN_BACKGOUND = "background";
	public static final String INTERVAL = "interval";
	public static final String MESSAGE_SND = "message_snd";
	public static final String ORDER_SND = "order_snd";
	public static final String VIBRATE = "vibrate";
	public static final String RECREATEORDER = "recreateorder";
	public static final String CAMERA_WIDTH = "camera_width";
	public static final String CAMERA_HEIGHT = "camera_height";
	public static final String PAUSE = "pause";

	protected int getSettingId(){
		return R.xml.setting;
	}
	
	protected String[] getPrefs(){
		return new String[0];
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SHARED_PREFERENCES_NAME);
		addPreferencesFromResource(getSettingId());
		
		for (String p : getPrefs()) {
			Preference pref = findPreference(p);
			
			if(pref != null)
			{
				pref.setSummary(
					pref.getSharedPreferences().getString(pref.getKey(), ""));
				
				pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						preference.setSummary((CharSequence)newValue);
						
						return true;
					}
				});
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		((ConfigReader)ConfigManager.getConfig()).loadConfig();
	}
}
