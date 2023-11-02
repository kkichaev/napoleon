package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="PayTime", keyFields="id")
public class PayTime extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override
	public String toString() { return name;	}
}
