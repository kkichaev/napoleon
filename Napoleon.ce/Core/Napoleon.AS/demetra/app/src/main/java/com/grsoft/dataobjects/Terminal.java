package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="terminal", keyFields="id")
@ServerInfo(name="Terminal")
public class Terminal extends DataObject {
	public String id = "";
	public String number = "";
}
