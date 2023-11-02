package com.grsoft.napoleon;

import java.util.Date;

import android.content.SharedPreferences;

public class MainEx extends Main {
	
	public static final String LAST_CONNECT = "connection";
	public static final String CONNECT_PREF = "cpref";

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences sp = getSharedPreferences(CONNECT_PREF, MODE_PRIVATE);
		long curTime = new Date().getTime();
		long lastConnect = sp.getLong(LAST_CONNECT, curTime);
		
		if( Math.abs(lastConnect - curTime) > 3600l * 24 * 1000 ) {
			UpdateDB.open(this);
		}
	}
}
