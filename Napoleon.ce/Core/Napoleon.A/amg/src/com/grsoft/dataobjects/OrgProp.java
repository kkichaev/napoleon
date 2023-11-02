package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orgprop", keyFields="id")
@ServerInfo(name="OrgProp")
public class OrgProp extends DataObject {
	public String id = "";
	public String matrix = "";
	public int script = 0;
}
