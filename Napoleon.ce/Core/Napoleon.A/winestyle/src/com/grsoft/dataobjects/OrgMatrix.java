package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="OrgMatrix")
@TableInfo(name="orgmatrix", keyFields="id,id_i", indexes="id_i")
public class OrgMatrix extends DataObject {
	public String id = "";
	public String id_i = "";
}
