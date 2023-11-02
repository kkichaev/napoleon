package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Dover", keyFields="number")
@ServerInfo(name="DoverNumber")
public class Dover extends DataObject {
	public String number = "";
	
	@Override public String toString() { return number; }
}
