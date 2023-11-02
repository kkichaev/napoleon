package com.grsoft.database;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.ServerInfoObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ServerInfoHitchingChinkova extends Hitching {
	
	static long dateDiff = 0;
	
	public static Date getLocalDate(Date serverDate) {
		return new Date(serverDate.getTime() - dateDiff);
	}
	
	public ServerInfoHitchingChinkova() {
		super(ServerInfoObject.class, "%ServerInfo");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ServerInfoObject obj = (ServerInfoObject) rawObject.createDataObject(dataObject);
		Date localDate = new Date();
		dateDiff = obj.curdate.getTime() - localDate.getTime();

//		Calendar c = Calendar.getInstance();
//		int tz = c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET); 		
//		dateDiff = obj.curdate.getTime() - localDate.getTime() + (obj.serverTimeZone * 60 * 1000 + tz);
	}
}
