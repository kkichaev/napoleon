package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="zone", keyFields="id")
public class Zone extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override
	public String toString() { return name; }
}
