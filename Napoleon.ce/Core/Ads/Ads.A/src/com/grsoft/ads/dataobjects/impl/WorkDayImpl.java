package com.grsoft.ads.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

import com.grsoft.ads.dataobjects.WorkDay;
import com.grsoft.ads.utils.AdsLog;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.util.Util;

public class WorkDayImpl extends DbObject<WorkDay>{
	
	public boolean isWorkTimeActive(){
		return data.active != 0; 
	}
	
	public void startWork(){
		LogImpl.log(AdsLog.STARTWORKING);
		data.active = 1;
	}
	
	public void endWork(){
		LogImpl.log(AdsLog.ENDWORKING);
		data.active = 0;
	}
	
	public static boolean closePrevDay(){
		boolean result = false;
		DbWriter.checkDBTable(WorkDay.class);
		try{
			Cursor cursor = DataBaseManager.getDataBase().query(
				DataObjectInfo.getInstance().getTableName(WorkDay.class), 
				new String[] {"date", "active", "end"}, "date < ?", 
				new String[] {Long.toString(Util.getDate().getTime())}, 
				null, null, "date DESC");
			try{
				if (cursor.moveToFirst()){
					int act = cursor.getInt(cursor.getColumnIndex("active"));
					long dt = cursor.getLong(cursor.getColumnIndex("date"));
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(dt));
					calendar.add(Calendar.HOUR_OF_DAY, 23);
					calendar.add(Calendar.MINUTE, 59);
					
					if (act == 1){
						ContentValues cv = new ContentValues();
						cv.put("active", 0);
						cv.put("end", calendar.getTime().getTime());
						
						DataBaseManager.getDataBase().update(
								DataObjectInfo.getInstance().getTableName(WorkDay.class),
								cv, "date=?", new String[] {Long.toString(dt)});
						
						result = true;
					}
				}
				
				return result;
			}finally{
				cursor.close();
			}
		}catch(Exception e){
			e.printStackTrace();
			return result;
		}
	}
	
	public boolean closeEndStartPrevDay()
	{
		boolean result = false;
		
		if (closePrevDay())
		{
			startWork();
			write();
			close();
			result = true;
		}
		
		return result;
	}
	
	@Override
	public boolean read() {
		boolean result = super.read();
		
		if (!result)
			result = closeEndStartPrevDay();
		
		return result;
	}
	
	@Override
	public boolean read(long rowid) {
		boolean result = super.read(rowid);
		
		if (!result)
			closeEndStartPrevDay();
		
		return result;
	}
	
	@Override
	public boolean read(long rowid, boolean useCache) {
		boolean result = super.read(rowid, useCache);
		
		if (!result)
			closeEndStartPrevDay();
		
		return result;
	}
}
