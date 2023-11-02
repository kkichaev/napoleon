package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="ItemGroup", keyFields="id")
@ServerInfo(name="ItemGroups")
public class ItemGroup extends DataObject {
	public String id = "";
	public String name = "";
}
