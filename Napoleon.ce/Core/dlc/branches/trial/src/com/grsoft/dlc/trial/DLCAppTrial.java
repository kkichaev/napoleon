package com.grsoft.dlc.trial;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import com.grsoft.dlc.DLCApp;
import com.grsoft.dlc.Preferences;

public class DLCAppTrial extends DLCApp {
	private static final String FIRST_TIME_START = "firsttimestart";
	public static final String LICENSED = "licensed"; 
	
	@Override
	public void onCreate() {
		super.onCreate();
		startService(new Intent(this, CloseActivityService.class));
		
		SharedPreferences pref = getSharedPreferences(
				Preferences.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		long fts = pref.getLong(FIRST_TIME_START, -1);
		
		if (fts == -1 ){
			Editor editor = pref.edit();
			editor.putLong(FIRST_TIME_START, Calendar.getInstance().getTime().getTime());
			editor.commit();
		}
	}
	
	@Override
	public boolean isFreeVersion() {
		boolean result = false;
		
		if (!isLicensed() && isTrialExpired()){
			Toast.makeText(getApplicationContext(), 
					R.string.trial_end_message, Toast.LENGTH_LONG).show();
			result = true;
		}
		
		return result;
	}

	public boolean isLicensed() {
		SharedPreferences pref = getSharedPreferences(
				Preferences.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		return pref.getBoolean(LICENSED, false);
	}
	
	private boolean isTrialExpired(){
		final int EXPIRED = 2;
		final long DAYS = 24 * 60 * 60 * 1000L;
		long ms = millisecondTrialCount();
		
		//Toast.makeText(getApplicationContext(), String.format("\n%d\n", ms / DAYS),Toast.LENGTH_LONG).show();
		
		boolean result = ( ms / DAYS) >= EXPIRED;
		
		if (!result){
			final long HOURS = 60 * 60 * 1000L;
			final long HPAST = ms / HOURS;
			final long HREM = 48 - HPAST;
			
			StringBuilder sb = new StringBuilder();
			sb.append(getResources().getString(R.string.time_expiration_msg)).append('\n');
			sb.append(String.format(getResources().getString(R.string.time_pass), HPAST)).append('\n');
			sb.append(String.format(getResources().getString(R.string.time_remn), HREM)).append('\n');
			
			Toast.makeText(getApplicationContext(), sb.toString(),Toast.LENGTH_LONG).show();
		}
		
		return result;
	}

	public long millisecondTrialCount() {
		SharedPreferences pref = getSharedPreferences(
				Preferences.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		long fts = pref.getLong(FIRST_TIME_START, -1);
		Date now = Calendar.getInstance().getTime();
		long ms = Math.abs((now.getTime() - fts));
		return ms;
	}
}
