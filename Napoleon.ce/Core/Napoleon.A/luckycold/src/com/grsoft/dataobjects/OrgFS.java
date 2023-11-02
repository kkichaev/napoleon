package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="org_folders", keyFields="id")
public class OrgFS extends DataObject {
	public int id;
	public String name;
	public int level;
	public String fid;
}
