package com.grsoft.dlc;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	public static final String OPEN_COMMAND = "com.grsoft.dls.PREFERENCES";
	public static final String SHARED_PREFERENCES_NAME = "preferences";
	public static final String PASSWORD = "password";
	public static final String ALLOW_PHONE = "phone";
	public static final String ALLOW_MESSAGE = "message";
	public static final String SHOW_DCL_ICONS = "show_dcl_icons";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getPreferenceManager().setSharedPreferencesName(SHARED_PREFERENCES_NAME);
		addPreferencesFromResource(R.xml.preference);
	}
}
