package com.grsoft.napoleon;

import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.SettingActivity;

public class SettingEx extends Setting {
	@SuppressWarnings("deprecation")
	@Override
	protected void updatesTabs(boolean isAdmin) {
		tabsActivities.clear();
		createTabSpec(NetworkSettingActivity);
		
		if (RuntimeEnv.isPhotoSupported())
			createTabSpec(PhotoSettingActivity);
		
//		if( isAdmin ) {
//			createTabSpec(BehaviorSettingActivity);
//			createTabSpec(GPSSettingActivity);
//		}
		
		createTabSpec(WarehouseSettingActivity);
		
		for(Class<? extends SettingActivity> ca : addTabs)
			createTabSpec(ca);
		
		if( openTag != null )
			getTabHost().setCurrentTabByTag(openTag);
	}
}
