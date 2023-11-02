package com.grsoft.napoleon;

import com.grsoft.util.SettingActivity;

public class SettingEx extends Setting {
	public static boolean OpenAsAdmin = false;
	
	@Override
	protected void updatesTabs(boolean isAdmin) {
		OpenAsAdmin = isAdmin;
		super.updatesTabs(isAdmin);
	}
	
	@Override
	protected boolean canCreateForUser(Class<? extends SettingActivity> activity) {
		return true;
	}
}
