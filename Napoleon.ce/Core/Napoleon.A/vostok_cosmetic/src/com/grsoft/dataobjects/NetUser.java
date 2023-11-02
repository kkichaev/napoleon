package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="netuser", keyFields="id")
public class NetUser extends DataObject {
	public String id;
	public String userid;
}
