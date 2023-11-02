package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;

@TableInfo(name="IncRights", keyFields="id")
public class IncassRights extends DataObject {

	public String id = "";
	public String userid = "";
	
	public int dvr = 0;
	public int bank = 0;
	
	public static IncassRights get() {
		IncassRights ret = new IncassRights();
		
		DbReader r = new DbReader();
		r.select(ret, DataObjectInfo.getInstance().getTableName(ret.getClass()), "id=userid");
		r.close();
		
		return ret;
	}
}
