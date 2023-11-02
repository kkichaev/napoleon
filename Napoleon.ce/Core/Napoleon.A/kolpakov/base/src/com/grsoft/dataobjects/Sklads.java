package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Sklads", keyFields="id")
public class Sklads extends DataObject {
	public String id;
	public String name;
	
	public int def;
	public int van;
	public String idOrg;
	public String priceid;
	
	@Override
	public String toString() {
		return name;
	}
}
