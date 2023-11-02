package com.grsoft.napoleon.rfid;

import android.content.Context;

import com.senter.support.openapi.StUhf.Q;

public class Config {
	public Q q;
	
	public static Config load(Context context) {
		Config c = new Config();
		c.readConfig(context.getApplicationContext());
		
		return c;
	}

	private void readConfig(Context applicationContext) {
		q = Q.values()[3];
	}
}
