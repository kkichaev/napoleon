package com.grsoft.napoleon;


public class SettingEx extends Setting {
	@Override
	protected void updatesTabs(boolean isAdmin) {
		tabsActivities.clear();
		if( isAdmin ) {
			createTabSpec(NetworkSettingActivity);
			createTabSpec(BehaviorSettingActivity);
		}
	}
}
