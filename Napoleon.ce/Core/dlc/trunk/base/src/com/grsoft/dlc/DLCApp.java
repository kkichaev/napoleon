package com.grsoft.dlc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.grsoft.dlc.database.DataBaseManager;

public class DLCApp extends Application {
	private static Map<String, ApplicationInfo> appList;
	public DataBaseManager dbManager;
	public static String myPackage;
	
	public static String PKG_SET_CHANGED = "pkg_set_changed";
	
	public void onCreate() {
		dbManager = new DataBaseManager(this);
		dbManager.getWritableDatabase();
		
		myPackage = getPackageName();
	};
	
	public synchronized Map<String, ApplicationInfo> getAppList(){
		boolean recreate = getSharedPreferences(Preferences.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.getBoolean(PKG_SET_CHANGED, true);
		
		return getAppList(recreate);
	}
	
	private synchronized Map<String, ApplicationInfo> getAppList(boolean recreate){
		
		if(appList == null || recreate)
			appList = createAppList();
		
		SharedPreferences pref = getSharedPreferences(Preferences.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor e = pref.edit();
		e.putBoolean(PKG_SET_CHANGED, false);
		e.commit();
		
		return appList;
	}
	
	private Map<String, ApplicationInfo> createAppList() {
		Map<String, ApplicationInfo> result = new HashMap<String, ApplicationInfo>();
		
		PackageManager manager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        
        if (apps != null) {
            final int count = apps.size();

            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo(this);
                ResolveInfo info = apps.get(i);

                application.title = info.loadLabel(manager);
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);
                application.init();
                
                result.put(info.activityInfo.applicationInfo.packageName, application);
            }
        }
        
		return result;
	}
	
	public boolean isFreeVersion(){
		return true;
	}
}
