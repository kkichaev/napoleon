package com.grsoft.ads;

import android.content.Context;
import android.content.Intent;

public class SettingUser extends SettingDataBase {
	@Override
	protected int getSettingId() {
		return R.xml.setting_user;
	}

	public static void open(Context context) {
		Intent intent = new Intent(context, SettingUser.class);
		context.startActivity(intent);
	}
	
	@Override
	protected String[] getPrefs() {
		return new String[]{SERV_ADR_1, SERV_ADR_2, PORT};
	}
}
