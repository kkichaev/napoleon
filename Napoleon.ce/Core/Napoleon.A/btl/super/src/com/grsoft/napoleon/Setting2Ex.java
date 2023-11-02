package com.grsoft.napoleon;

public class Setting2Ex extends SettingEx {
	public void createNetworkSettingTab() {
		createTabSpec(ConfigurationEx.class, "Сеть", "network", R.drawable.setting_network);
	}
}
