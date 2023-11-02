package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Dover", keyFields="id")
public class Dover extends DataObject {
	public String id = "";
	public String name = "";
	public String firm = "";
	
	@Override public String toString() { return name; }
}
