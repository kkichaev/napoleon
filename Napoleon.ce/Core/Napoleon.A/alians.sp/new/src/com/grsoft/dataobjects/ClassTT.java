package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="classtt", keyFields="id")
@ServerInfo(name="ClassTT")
public class ClassTT extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override
	public String toString() {
		return name;
	}
}
