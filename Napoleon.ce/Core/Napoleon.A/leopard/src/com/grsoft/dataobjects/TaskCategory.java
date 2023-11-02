package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="taskcat",keyFields="name")
public class TaskCategory extends DataObject {
	public String name="";
}
