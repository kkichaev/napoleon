package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="colors",keyFields="id")
public class Colors extends DataObject {
	public int id;
	public String name;
}
