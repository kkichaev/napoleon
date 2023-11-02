package com.grsoft.napoleon;

import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.SettingActivity;

public class SettingEx extends Setting {
	
	@Override
	protected void updatesTabs(boolean isAdmin) {
		tabsActivities.clear();
		Class<? extends SettingActivity> settings = isAdmin ? Configuration.class : ConfigurationEx.class;
		createTabSpec(settings);
		
		if (RuntimeEnv.isPhotoSupported())
			createTabSpec(PhotoSetting.class);
		
		createTabSpec(BehaviorSettingActivity);
		
		if( isAdmin )
			createTabSpec(GPSSettingActivity);
		
		createTabSpec(WarehouseSettingActivity);
		
		for(Class<? extends SettingActivity> ca : addTabs)
			createTabSpec(ca);
		
		if( openTag != null )
			getTabHost().setCurrentTabByTag(openTag);
	}
}
