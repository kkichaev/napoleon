package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="SelectMatrix", keyFields="id")
public class SelectMatrix extends DataObject {
	public String id = "";
	public String name = "";
}
