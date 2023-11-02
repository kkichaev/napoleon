package com.grsoft.dataobjects.impl;

import java.util.Date;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ReportAnswerSPK;

public class ReportAnswerSPKImpl extends DbObject<ReportAnswerSPK> {
	
	/***
	 * Дата отчета
	 * @param name
	 * @return
	 */
	public static Date getAnswerDate(String name){
		Date result = null;
		DbWriter.checkDBTable(ReportAnswerSPK.class);
		SQLiteDatabase db = DataBaseManager.getDataBase();
		android.database.Cursor c = db.query(DataObjectInfo.getInstance().getTableName(ReportAnswerSPK.class), 
				new String[]{"created"}, "name=?", 
				new String[]{name}, null, null, null);
		
		if (c.moveToFirst()){
			long time = c.getLong(c.getColumnIndex("created"));
			result = new Date(time);
		}
		
		c.close();
		
		return result;
	}
}
