package com.grsoft.ads;

public class SettingNetwork extends Setting {
	@Override
	protected int getSettingId() {
		return R.xml.setting_network;
	}
	
	@Override
	protected String[] getPrefs() {
		return new String[]{LOGIN, PASSW, SERV_ADR_1, SERV_ADR_2, PORT};
	}
}
