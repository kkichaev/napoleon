package com.grsoft.napoleon;

import com.grsoft.util.SettingActivity;

public class SettingEx extends Setting {
	@Override
	protected boolean canCreateForUser(Class<? extends SettingActivity> activity) {
		return activity != BehaviorSettingActivity && super.canCreateForUser(activity);
	}
}
