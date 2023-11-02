package com.grsoft.dlc.trial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.grsoft.dlc.ApplicationInfo;
import com.grsoft.dlc.DLCApp;
import com.grsoft.dlc.Features;

public class CloseActivityService extends Service {
	List<String> allowpkg = new ArrayList<String>();
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		allowpkg.clear();
		allowpkg.add("com.android.packageinstaller");
	}

	private boolean terminate(Map<String, ApplicationInfo> apps, String pkg){
		boolean result = true;
		
		result = !allowpkg.contains(pkg);
		
		if (result && apps.containsKey(pkg))
			result = !apps.get(pkg).isAllowed();
		
		return result;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		new Thread(new Runnable() {
			public void run() {
				ActivityManager activityManager = (ActivityManager) getSystemService("activity");
				while (true) {
					Map<String, ApplicationInfo> apps = ((DLCApp)(getApplication())).getAppList();
					
					if(apps != null){
						try {
							for (RunningTaskInfo info : activityManager
									.getRunningTasks(1)) {
								
								if(Features.DATE_TIME_SETTING)
									if (info.topActivity.getClassName().equals("com.android.settings.DateTimeSettings"))
											continue;
								
								String pkgName = info.topActivity.getPackageName(); 
								if (!pkgName.equals(DLCApp.myPackage) && terminate(apps, pkgName)) {
									Log.d(getClass().getName(), pkgName);
									Intent startMain = new Intent(
											Intent.ACTION_MAIN);
									startMain.addCategory(Intent.CATEGORY_HOME);
									startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(startMain);
									Thread.sleep(400);
									activityManager.killBackgroundProcesses(pkgName);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try{
							Thread.sleep(100);
						}catch(Exception e){ e.printStackTrace(); }
					}
				}
			}
		}).start();
	}

}
