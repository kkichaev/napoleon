package com.grsoft.napoleon;

import com.grsoft.database.DataBaseManager;

import android.database.Cursor;

public class StopHelper {
	public static boolean stopDelivery(String id) {
		boolean stopped = false;
		String sql = "select count(*) as ctr from delivery where sumd > 0 and id = '" + id + "'";
		Cursor c = null;
		try {
			c = DataBaseManager.getDataBase().rawQuery(sql, null);
			while(c.moveToNext()) {
				stopped = c.getInt(0) >= 2;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		if( c != null)
			c.close();		
		
		return stopped;
	}
}
