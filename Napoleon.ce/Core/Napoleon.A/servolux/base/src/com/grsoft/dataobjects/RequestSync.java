package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="RequestSync")
@ServerInfo(name="RequestSync")
public class RequestSync extends DataObject {
	public int sync = 0;
	
	public static boolean needSync() {
		boolean ret = false;
		RequestSync data = new RequestSync();
		DbReader r = new DbReader();
		
		if(r.select(data, data.getTableName(), "")) {
			ret = data.sync > 0;
		}
		
		return ret;
	}
}
