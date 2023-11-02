package com.grsoft.dlc.trial;

import java.util.Map;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.grsoft.dlc.ApplicationInfo;
import com.grsoft.dlc.DLCApp;

public class CloseActivityService extends Service {
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private boolean terminate(Map<String, ApplicationInfo> apps, String pkg){
		boolean result = true;
		
		if (apps.containsKey(pkg))
			result = !apps.get(pkg).isAllowed();
		
		return result;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		new Thread(new Runnable() {
			public void run() {
				ActivityManager activityManager = (ActivityManager) getSystemService("activity");
				while (true) {
					Map<String, ApplicationInfo> apps = ((DLCApp)(getApplication())).getAppList();
					
					if(apps != null){
						try {
							for (RunningTaskInfo info : activityManager
									.getRunningTasks(1)) {
								String pkgName = info.topActivity.getPackageName(); 
								if (!pkgName.equals(DLCApp.myPackage) && terminate(apps, pkgName)) {
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
