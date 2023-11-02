package com.grsoft.dlc.trial;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.widget.Toast;

import com.grsoft.dlc.DLCApp;
import com.grsoft.dlc.Preferences;
import com.grsoft.dlc.R;

public class DLCAppTrial extends DLCApp {
	private static final String FIRST_TIME_START = "firsttimestart";
	public static final String LICENSED = "licensed"; 
	
	@Override
	public void onCreate() {
		super.onCreate();
		
//		boolean cont = true;
//		
//		while(cont){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
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
		boolean result = pref.getBoolean(LICENSED, false);
		final String LICENCED_PROJECT = "Novotek";
		
		if(!result){
			try{
				String pkjName = "com.grsoft.napoleon";
				PackageManager manager = getPackageManager();
				Resources resources = manager.getResourcesForApplication(pkjName);
				int resID = resources.getIdentifier("project", "string", pkjName);
				String version = resources.getString(resID);
				
				if(version.equals(LICENCED_PROJECT)){
					Editor editor = pref.edit();
					editor.putBoolean(DLCAppTrial.LICENSED, true);
					editor.commit();
					
					result = true;
				}
					
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
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
