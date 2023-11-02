package com.grsoft.dlc.database;

import android.provider.BaseColumns;

public interface AllowedApp extends BaseColumns {
	public String TABLE_NAME = "allowed";
	public String PRIMARY_KEY = "name";
	public String CLASSNAME = "name";
	
	public String[] PROJECTION = new String[]{CLASSNAME};
}
