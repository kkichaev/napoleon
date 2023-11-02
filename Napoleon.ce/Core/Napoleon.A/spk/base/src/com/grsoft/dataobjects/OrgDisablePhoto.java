package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orgDisable", keyFields="id")
@ServerInfo(name="OrgDisablePhoto")
public class OrgDisablePhoto extends DataObject {
	public String id = "";
}
