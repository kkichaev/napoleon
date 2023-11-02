package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgtype", keyFields="id")
public class OrgType extends DataObject {
	public String id = "";
	public String name = "";
}
