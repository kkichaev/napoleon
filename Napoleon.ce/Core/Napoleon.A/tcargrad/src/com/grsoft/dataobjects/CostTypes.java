package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="CostTypes",keyFields="id")
public class CostTypes extends DataObject {
	public String id;
	public String name;
	public String userid;
}
