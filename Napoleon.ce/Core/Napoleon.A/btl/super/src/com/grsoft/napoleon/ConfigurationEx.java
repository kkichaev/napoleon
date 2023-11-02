package com.grsoft.napoleon;

import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigImpl2Ex;

public class ConfigurationEx extends Configuration {
	@Override
	protected String getLogin(Config config) {
		return ((ConfigImpl2Ex)config).login;
	}
	
	@Override
	protected String getPassw(Config config) {
		return ((ConfigImpl2Ex)config).passw;
	}
	
	@Override
	protected void setLogin(Config config, String login) {
		((ConfigImpl2Ex)config).login = login;
	}
	
	@Override
	protected void setPassword(Config config, String passw) {
		((ConfigImpl2Ex)config).passw = passw;
	}
}
