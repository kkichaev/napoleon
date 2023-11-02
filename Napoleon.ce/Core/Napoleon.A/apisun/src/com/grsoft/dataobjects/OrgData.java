package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orgdata", keyFields="id")
@ServerInfo(name="OrgData")
public class OrgData extends DataObject {
	public String id = "";
	public String befvisit = "";
	public String concurents = "";
}
