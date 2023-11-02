package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgmtx", keyFields="id")
public class OrgMtx extends DataObject {
	public String id = "";
	public String matrix = "";
}
