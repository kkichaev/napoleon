package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Regions", keyFields="id")
public class OrgRegion extends DataObject {
	public String id;
	public String name;
	public String parent;
}
