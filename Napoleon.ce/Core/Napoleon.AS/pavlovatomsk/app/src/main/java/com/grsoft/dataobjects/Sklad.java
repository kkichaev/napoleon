package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="sklad", keyFields="id")
@ServerInfo(name="Sklad")
public class Sklad extends DataObject {
	public String id = "";
	public String name = "";
	public int whIndex = 0;
	public int costType = 0;
	
	@Override
	public String toString() {
		return name;
	}
}
