package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="cities", keyFields="id")
public class City extends DataObject {
	public String id = "";
	public String name = "";
	public String idr = "";
}
