package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="retmtx", keyFields="id")
public class RetMtx extends DataObject {
	public String id = "";
	public String matrix = "";
}
