package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="sklad", keyFields="id")
@ServerInfo(name="Sklads")
public class Sklads extends DataObject {
	public String id = "";
	public String name = "";
	public String firm = "";
	
	@Override public String toString() { return name; }
}
