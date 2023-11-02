package com.grsoft.adsmanager;

import java.util.Date;
import java.util.Random;

import com.grsoft.ads.R;
import com.grsoft.adsmanager.dataobjects.MAgent;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.LoginData;
import com.grsoft.network.ServerCommand;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class AdsAppManager extends Application{
	static final String SYNC_TIME_TAG = "SyncTag";
	
	@Override
	public void onCreate() {
		super.onCreate();
		init();
		updateDuration();
		setProgrammVersion();
	}

	private void init() {
		Path.SHARED_FOLDER = "AdsManager";
		Path.init(this);
		DataBaseManager.init();
		
		DbObject.regNewDataType(Agent.class, MAgent.class);
	}

	private void updateDuration() {
		long syncTime = getSyncTime(getApplicationContext());
		long ct = (new Date()).getTime();
		if( ct - syncTime > 30 * 60 *1000 ) {
			ServerAnswer sa = new ServerAnswer();
			sa.response = 1;
			sa.message = String.format("%X", (new Random(SystemClock.uptimeMillis())).nextInt());
			LoginData.putDuration(sa, getApplicationContext());
		}
	}
	
	public static void putSyncTime(Context context, long time) {
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SYNC_TIME_TAG, Context.MODE_PRIVATE);
		sp.edit().putLong(SYNC_TIME_TAG, time).commit();
	}
	
	public static long getSyncTime(Context context) {
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SYNC_TIME_TAG, Context.MODE_PRIVATE);
		return sp.getLong(SYNC_TIME_TAG, 0);
	}

	
	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
			ServerCommand.Category = "managerPDA";
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
