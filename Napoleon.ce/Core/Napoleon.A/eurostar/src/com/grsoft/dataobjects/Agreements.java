package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="agreements", keyFields="id")
public class Agreements extends DataObject {
	public String id;
	public String idOrg;
	public String idFirm;
	public String name;
	public int cost;
	public int common;
	
	@Override public String toString() { return name; }
}
