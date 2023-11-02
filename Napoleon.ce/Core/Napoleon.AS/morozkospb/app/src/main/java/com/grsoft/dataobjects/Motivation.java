package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="Motivation")
@TableInfo(name="motivation",keyFields="id")
public class Motivation extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override
	public String toString() {
		return name;
	}
}
