package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Cash", keyFields="id")
public class Cash extends DataObject {
	public String id;
	public String firm;
	public String name;
	
	@Override public String toString() { return name; }
}
