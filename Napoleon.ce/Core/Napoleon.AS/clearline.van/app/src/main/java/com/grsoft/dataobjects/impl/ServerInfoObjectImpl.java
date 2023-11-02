package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.ServerInfoObject;

import android.os.SystemClock;

public class ServerInfoObjectImpl extends DbObject<ServerInfoObject> {
	
	public Date getValidDate() {
		Date result = null;
		
		if (read()) {
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
