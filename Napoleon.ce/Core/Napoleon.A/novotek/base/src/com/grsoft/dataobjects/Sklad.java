package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="sklads", keyFields="key")
@ServerInfo(name="Sklads")
public class Sklad extends DataObject {
	public String key;
	public String value;
	public int canDiv;
	public int cosType = 0;
	public int useDiscount = 0;
	
	@Override public String toString() { return value; }
}
