package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="sizes",keyFields="id")
public class Sizes extends DataObject {
	public int id;
	public int categoryid;
	public String name;
}
