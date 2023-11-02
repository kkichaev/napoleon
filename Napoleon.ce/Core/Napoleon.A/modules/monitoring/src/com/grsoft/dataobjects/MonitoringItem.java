package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="MonItems",keyFields="id")
public class MonitoringItem extends DataObject {
	public String id;
	public String name;
	public int flags;
	
	public boolean isOur() {
		return ((flags & 1) != 0);
	}
}
