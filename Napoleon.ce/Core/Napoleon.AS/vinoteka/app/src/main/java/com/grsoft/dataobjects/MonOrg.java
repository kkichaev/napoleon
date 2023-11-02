package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;

@TableInfo(name="MonOrg", keyFields="id")
public class MonOrg extends DataObject {
	public String id;

	public static boolean contains(Org o) {
		DbReader r = new DbReader();
		
		MonOrg mo = new MonOrg();
		boolean ret = r.select(mo, mo.getTableName(), "id='" + o.id + "'");
		r.close();
		
		return ret;
	}
}
