package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="OrgTypeMatrix")
@TableInfo(name="orgtypematrix", keyFields="id")
public class OrgTypeMatrix extends DataObject {
	public String id = "";
	public String matrix = "";
}
