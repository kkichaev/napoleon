package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Dutie", keyFields="id")
@ServerInfo(name="Dutie")
public class Dutie extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override
	public String toString() {
		return name;
	}
}
