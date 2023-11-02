package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orgprop", keyFields="id,suppl")
@ServerInfo(name="OrgProp")
public class OrgProp extends DataObject {
	public String id = "";
	public String suppl = "";
}
