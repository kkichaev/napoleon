package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import android.content.SharedPreferences;

public class DocumentsEx extends Documents {
	public static final String LAST_SYNC = "LastSyncPref";

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences sp = getSharedPreferences(LAST_SYNC, MODE_PRIVATE);
		long value = sp.getLong(LAST_SYNC, 0);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 7);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		
		if( value < c.getTime().getTime() ) {
			UpdateDBEx.openSyncAll(this);
		}
	}
}
