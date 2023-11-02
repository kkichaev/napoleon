package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PrcTypes", keyFields="id")
@ServerInfo(name="PrcTypes")
public class PrcTypes extends DataObject {
	public String id = "";
	public String name = "";
	public int useNac = 0;
	
	@Override public String toString() { return name; }
}
