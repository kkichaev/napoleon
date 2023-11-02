package com.keeper;

import java.io.File;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.keeper.db.DataBaseManager;

public class KeeperApp extends Application {
	
	public static final String AppName = "Keeper";
	private static final String D_TAG = "KeeperApp"; 
	public static DataBaseManager dbManager;
	public static String masterPassword = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(D_TAG, "begin onCreate");
		File localData = getAppDataDir();
		Log.d(D_TAG, "path to local data: " + localData.getAbsolutePath());
		Log.d(D_TAG, "local data directory is exists: " + localData.exists());
		boolean mkdirsResult = false;
		
		if (!localData.exists())
			mkdirsResult = localData.mkdirs();
		Log.d(D_TAG, "mkdirsResult: " + Boolean.toString(mkdirsResult));
		
		dbManager = new DataBaseManager(getApplicationContext());
		Log.d(D_TAG, "end onCreate");
	}

	public static File getAppDataDir(){
		File sdcard = Environment.getExternalStorageDirectory();
		return new File(sdcard, AppName);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(D_TAG, "onTerminate");
	}
}
