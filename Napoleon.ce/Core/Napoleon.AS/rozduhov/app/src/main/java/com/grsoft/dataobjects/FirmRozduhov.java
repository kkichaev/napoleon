package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="firms", keyFields="id")
public class FirmRozduhov extends DataObject {
	public String id;
	public String name;
	public int qty;
	public int cost;
}
