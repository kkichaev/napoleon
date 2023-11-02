package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ServerInfoObject;

import android.os.SystemClock;

public class ServerInfoObjectImpl extends DbObject<ServerInfoObject> {
	
	public Date getValidDate() {
		Date result = null;
		
		DbReader r = new DbReader();
		
		if (r.select(data, getTableName(), "")) {
			result = getData().time;
			long now = SystemClock.elapsedRealtime(); 
			
			if ( now < getData().elapsedTime)
				result = null;
			else	
				result.setTime(result.getTime() + (now - getData().elapsedTime));
		}
		
		close();
		
		return result;
	}
}
