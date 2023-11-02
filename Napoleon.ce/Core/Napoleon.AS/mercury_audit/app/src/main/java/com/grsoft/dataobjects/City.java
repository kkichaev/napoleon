package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="city", keyFields="id")
@ServerInfo(name="City")
public class City extends OrgFolders {
	public String id = "";
	public int flags;
}
