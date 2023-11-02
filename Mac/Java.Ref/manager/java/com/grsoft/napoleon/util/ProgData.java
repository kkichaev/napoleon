package com.grsoft.napoleon.util;

import com.grsoft.manager.R;

import android.content.Context;

public class ProgData {
	public static String getLogin() {
		Config cfg = ConfigManager.getConfig();
		return cfg.login;
	}
	
	public static String getProject(Context ctx) {
//		return "test";
		return ctx.getResources().getString(R.string.project);
	}
}
