package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="orggroup", keyFields="id")
public class OrgGroup extends DataObject {
	public String id = "";
	@Scale(value=1)
	public int level = 0;
	public String name = "";
}
