package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="dogovors", keyFields="id")
public class Dogovor extends DataObject {
	public String id;
	public String name;
	
	public int def;
	public String clientid;
	public String companyid;
	public String priceid;
	
	public String info = "";
	
	@Override
	public String toString() {
		return name;
	}

	public int black = 0;
}
