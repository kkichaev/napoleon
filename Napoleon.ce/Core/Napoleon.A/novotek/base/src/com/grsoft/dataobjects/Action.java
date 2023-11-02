package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Астион", keyFields="id")
public class Action extends DataObject {
	public String id = "";
	public String name = "";
}
