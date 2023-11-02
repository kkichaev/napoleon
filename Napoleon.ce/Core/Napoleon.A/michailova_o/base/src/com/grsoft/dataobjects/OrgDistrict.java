package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orgdistrict", keyFields="id")
@ServerInfo(name="OrgDistrict")
public class OrgDistrict extends DataObject {
	public String id = "";
	public int rejret = 0;
}
