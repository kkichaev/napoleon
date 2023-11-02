package com.ksoft.ardalarm;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Setting extends PreferenceActivity {
	public final static String ACTION = "com.kasoft.ardalarm.OPEN_SETTING";
	public static final String SHARED_PREFERENCES_NAME = "setting";
	public static final String MESSAGE_SND = "message_snd";
	public static final String ARD_ADDR = "ard_addr";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SHARED_PREFERENCES_NAME);
		addPreferencesFromResource(R.xml.setting);
	}
}
