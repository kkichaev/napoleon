package com.grsoft.manager;

import java.util.Date;
import java.util.Random;

import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.napoleon.QuestAttachmentsList;
import com.grsoft.network.LoginData;
import com.grsoft.network.ServerCommand;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

public abstract class ManagerApplicationBase extends Application {
	protected static final String TAG = "ManagerApp";
	
	static final String SYNC_TIME_TAG = "SyncTag";
	
	protected abstract void init();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		long syncTime = getSyncTime(getApplicationContext());
		long ct = (new Date()).getTime();
		if( ct - syncTime > 30 * 60 *1000 ) {
			ServerAnswer sa = new ServerAnswer();
			sa.response = 1;
			sa.message = String.format("%X", (new Random(SystemClock.uptimeMillis())).nextInt());
			LoginData.putDuration(sa, getApplicationContext());
		}
		init();		
		
		setProgrammVersion();
	}
	
	public static void putSyncTime(Context context, long time) {
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SYNC_TIME_TAG, Context.MODE_PRIVATE);
		sp.edit().putLong(SYNC_TIME_TAG, time).commit();
	}
	
	public static long getSyncTime(Context context) {
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SYNC_TIME_TAG, Context.MODE_PRIVATE);
		return sp.getLong(SYNC_TIME_TAG, 0);
	}

	protected void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
			ServerCommand.Category = "managerPDA";
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
