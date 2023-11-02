package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="ModifyOrg", keyFields="created")
public class ModifyOrg extends OrgData {
	public String orgid = "";
}
