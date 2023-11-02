package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Category", keyFields="id")
public class Category extends DataObject {
	public String id;
	public int level;
	public String name;
}
