package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="DogovorRoute", keyFields="id")
@ServerInfo(name="DogovorRoute")
public class DogovorRoute extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override
	public String toString() {
		return name;
	}
}
