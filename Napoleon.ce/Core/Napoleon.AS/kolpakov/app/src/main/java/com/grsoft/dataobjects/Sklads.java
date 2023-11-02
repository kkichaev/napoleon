package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Sklads", keyFields="id")
public class Sklads extends DataObject {
	public String id;
	public String name;
	
	public int def;
	public String idOrg = "";
	public String priceid = "";
	public int index = 0;
	public int van = 0;
	
	@Override
	public String toString() {
		return name;
	}
}
