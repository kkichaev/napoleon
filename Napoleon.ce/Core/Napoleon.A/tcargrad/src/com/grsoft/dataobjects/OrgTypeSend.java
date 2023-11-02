package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgTypeSend", keyFields="id")
public class OrgTypeSend extends DataObject {
	public String id;
	public String type;
}
