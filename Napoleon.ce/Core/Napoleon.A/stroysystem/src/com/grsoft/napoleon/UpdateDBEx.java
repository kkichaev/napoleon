package com.grsoft.napoleon;

import java.util.Date;

import android.content.SharedPreferences;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void postSync(Boolean result) {
		if(result) {
			SharedPreferences sp = getSharedPreferences(MainEx.CONNECT_PREF, MODE_PRIVATE);
			long curTime = new Date().getTime();
			SharedPreferences.Editor e = sp.edit();
			e.putLong(MainEx.LAST_CONNECT, curTime);
			e.clear().commit();
		}
	}
}
