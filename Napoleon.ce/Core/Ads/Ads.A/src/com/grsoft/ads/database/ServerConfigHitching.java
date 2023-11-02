package com.grsoft.ads.database;

import com.grsoft.ads.Ads;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.impl.ConfigImpl;

public class ServerConfigHitching extends Hitching {

	public ServerConfigHitching() {
		super(com.grsoft.dataobjects.Config.class, "ServerConfig");
		DbWriter.checkDBTable(Config.class);
		ConfigImpl configImpl = new ConfigImpl();
		configImpl.delete("key = '" + Ads.ADMPWD + "'", null);
	}

}
