package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="akbdata", keyFields="userid")
@ServerInfo(name="AKBData")
public class AKBData extends DataObject {
	public String userid = "";
	public int alldoc = 0;
	public int inroute = 0;
}
