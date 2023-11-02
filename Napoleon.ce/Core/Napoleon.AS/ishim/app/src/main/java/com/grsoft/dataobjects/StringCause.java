package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="stringcause", keyFields="id")
@ServerInfo(name="StringCause")
public class StringCause extends DataObject {
	public String id = "";
	public String text = "";
	
	@Override
	public String toString() {
		return text;
	}
}
