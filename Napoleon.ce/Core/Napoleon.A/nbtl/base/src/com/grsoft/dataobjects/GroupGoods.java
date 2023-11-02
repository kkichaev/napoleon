package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="groupgoods", keyFields="id")
@ServerInfo(name="GroupGoods")
public class GroupGoods extends DataObject {
	public String id = "";
	public String fid = "";
	public String name;
}
