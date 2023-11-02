package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="action", keyFields="id")
@ServerInfo(name="Action")
public class Action extends DataObject {
	public String id = "";
	public String name = "";
	public int bonus = 0;
}
