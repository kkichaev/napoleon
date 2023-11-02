package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="FolderPresent", keyFields="id")
public class FolderPresent extends DataObject {
	public String id = "";
	public String path = "";
	public String color = "";
	public int tsz = 0;
}
