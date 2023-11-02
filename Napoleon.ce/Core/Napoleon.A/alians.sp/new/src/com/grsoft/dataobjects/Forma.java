package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="forma", keyFields="id")
@ServerInfo(name="Forma")
public class Forma extends DataObject {
	public String id = "";
	public String name = "";
	public int innLength = 0;
	
	@Override
	public String toString() {
		return name;
	}
}
