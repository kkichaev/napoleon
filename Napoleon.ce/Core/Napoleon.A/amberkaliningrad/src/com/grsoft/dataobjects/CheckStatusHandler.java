package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.DataBaseManager;

import android.database.sqlite.SQLiteStatement;

public class CheckStatusHandler {
	SQLiteStatement stmt;
	
	public CheckStatusHandler() {
		RequestChek rc = new RequestChek();
		String sql = "update \"" + rc.getTableName() + "\" set handleStatus=? where created=?";
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
		
		stmt.clearBindings();
		stmt.bindLong(1, value);
		stmt.bindLong(2, created.getTime());
		stmt.execute();
	}
}
