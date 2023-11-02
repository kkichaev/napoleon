package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DivisionManager;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

public class DivisionManagerImpl extends DbObject<DivisionManager>{
	public static boolean isMobile() {
		Config config = ConfigManager.getConfig();
		DivisionManagerImpl dm = new DivisionManagerImpl();
		dm.read("login", config.login);
		dm.close();
//		return true;
		return dm.getData().mobile == 1;
	}
}
