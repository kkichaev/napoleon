package com.grsoft.napoleon.util;

import java.util.Date;

import com.grsoft.util.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class SyncShedulHelper {
	
	static String PREF_NAME = "SyncPrf";
	static String KEY_NAME = "SyncDate";
	
	public static boolean needSync(Context context) {
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		long s = sp.getLong(KEY_NAME, 0);
		long ct = Util.getDate().getTime();
		
		return (ct - s) >= 24*3600*1000;
	}
	
	public static void markSync(Context context) {
		SharedPreferences.Editor e = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
		e.putLong(KEY_NAME, (new Date()).getTime());
		e.commit();
	}
}
