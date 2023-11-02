package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="region", keyFields="id")
public class Region extends DataObject {
	public String id = "";
	public String name = "";
}
