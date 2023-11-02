package com.grsoft.napoleon;

import com.grsoft.util.SettingActivity;

public class SettingEx extends Setting {
	@Override
	protected void createTabSpec(Class<? extends SettingActivity> tabPage) {
		if( tabPage == NetworkSettingActivity )
			super.createTabSpec(tabPage);
	}
}
