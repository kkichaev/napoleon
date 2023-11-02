package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="FocusRejectReason")
@TableInfo(name="FocusRejectReason", keyFields="id")
public class FocusRejectReason extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override public String toString() { return name; }
}
