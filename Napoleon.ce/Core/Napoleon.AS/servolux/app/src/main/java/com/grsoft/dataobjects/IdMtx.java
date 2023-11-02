package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="idmtx", keyFields="id,firm")
@ServerInfo(name="IdMtx")
public class IdMtx extends DataObject {
	public String id = "";
	public String firm = "";
	public String mtx = "";
}
