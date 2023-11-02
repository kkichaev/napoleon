package com.grsoft.ads.util;

import java.util.Date;

import com.grsoft.ads.dataobjects.SyncInfo;
import com.grsoft.ads.dataobjects.impl.SyncInfoImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.util.Util;

public class SyncInfoUtil {
	public static Date getLastSync(){
		Cursor<SyncInfo> cursor = new Cursor<SyncInfo>(new SyncInfoImpl(), "", "date ASC");
		cursor.close();
		Date result = cursor.current().getData().date;
		
		if (result == null)
			result = new Date(0, 0, 1);
		
		return result;
	}
	
	public static boolean setLastSync(int traffic){
		SyncInfoImpl sim = new SyncInfoImpl();
		sim.setCloseAfterWrite(true);
		sim.getData().date = Util.getDateTime();
		sim.getData().traffic = traffic;
		return sim.write() != -1;
	}
}

