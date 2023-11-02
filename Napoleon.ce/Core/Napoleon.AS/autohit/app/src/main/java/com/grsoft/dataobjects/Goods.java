package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Goods", keyFields="id")
public class Goods extends DataObject {
	public String id = "";
	public String fid;
	public String name = "";
}
