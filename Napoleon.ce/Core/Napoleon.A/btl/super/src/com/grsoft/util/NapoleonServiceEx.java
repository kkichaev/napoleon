package com.grsoft.util;

import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigImpl2Ex;
import com.grsoft.network.LoginData;

public class NapoleonServiceEx extends NapoleonService {
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	protected LoginData getUserInfo(Config config) {
		ConfigImpl2Ex configEx = (ConfigImpl2Ex)config;
		return new LoginData(configEx.login, configEx.passw, this);
	}
}
