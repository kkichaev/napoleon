package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="CustomCost", keyFields="id")
public class CustomCost extends DataObject {
	public String id;
	public String userid;
}
