package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="NoOrderReason", keyFields="id")
@ServerInfo(name="NoOrderReason")
public class NoOrderReason extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override public String toString() { return name; }
}
