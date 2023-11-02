package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="DocUserStatus", keyFields="name")
public class DocUserStatus extends DataObject {
	public String name = "";
	public String pic = "";
}
