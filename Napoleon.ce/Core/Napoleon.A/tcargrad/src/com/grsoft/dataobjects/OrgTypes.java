package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgType", keyFields="type")
public class OrgTypes extends DataObject {
	public String type;
	public String name;
}
