package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orgmatrix", keyFields="id")
@ServerInfo(name="OrgMatrix")
public class OrgMatrix extends DataObject {
	public String id = "";
	public String mtx = "";
}
