package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OrgMatrix", keyFields="cdef,id")
@ServerInfo(name="OrgMatrix")
public class OrgMatrix extends DataObject {
	public String id = "";
	public String cdef= "";
	public String name = "";
}
