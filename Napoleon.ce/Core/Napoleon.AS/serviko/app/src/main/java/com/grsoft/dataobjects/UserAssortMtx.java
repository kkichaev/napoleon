package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="userassortmtx", keyFields="matrix")
@ServerInfo(name="UserAssortMtx")
public class UserAssortMtx extends DataObject {
	public String matrix = "";
}
