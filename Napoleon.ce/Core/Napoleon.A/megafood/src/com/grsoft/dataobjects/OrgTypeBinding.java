package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OrgTypeBinding", keyFields="id")
@ServerInfo(name="OrgTypeBinding")
public class OrgTypeBinding extends DataObject {
	public String id = "";
	public String type = "";
	
	public static String getType(String id) {
		OrgTypeBinding ret = new OrgTypeBinding();
		DbReader r = new DbReader();
		r.select(ret, ret.getTableName(), "id='" + id + "'");
		r.close();
		return ret.type;
	}
}
