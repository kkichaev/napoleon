package com.grsoft.dataobjects;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.database.DataBaseManager;

import android.database.sqlite.SQLiteStatement;

public class CheckStatusHandler {
	SQLiteStatement stmt;
	
	public CheckStatusHandler() {
		RequestChek rc = new RequestChek();
		String sql = "update \"" + rc.getTableName() + "\" set handleStatus=?, handleChanged=? where created=?";
		try {
			stmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		if( stmt != null ) {
			stmt.close();
			stmt = null;
		}
	}
	
	public void update(Date created, int value) {
		if( stmt == null)
			return;
		
		Calendar c = Calendar.getInstance();
		
		stmt.clearBindings();
		stmt.bindLong(1, value);
		stmt.bindLong(2, c.getTime().getTime());
		stmt.bindLong(3, created.getTime());
		stmt.execute();
	}
}
