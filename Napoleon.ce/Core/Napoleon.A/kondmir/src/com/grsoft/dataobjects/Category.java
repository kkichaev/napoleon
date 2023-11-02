package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="category", keyFields="id")
@ServerInfo(name="Category")
public class Category extends DataObject{
	public String id = "";
	public String name = "";
}
