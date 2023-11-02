package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="OrgMtx")
@TableInfo(name="orgmtx", keyFields="id")
public class OrgMtx extends DataObject {
	public String id = "";
	public String matrix = "";
}
