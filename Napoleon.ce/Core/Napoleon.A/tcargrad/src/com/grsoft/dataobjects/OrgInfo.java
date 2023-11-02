package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OrgInfo", keyFields="id")
@ServerInfo(name="OrgInfo")
public class OrgInfo extends DataObject {
	public String id = "";
	public String info = "";
}
