package com.grsoft.database;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DlvDebet;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.sqlite.SQLiteStatement;

public class DlvSumHitching extends Hitching {
	SQLiteStatement stmt;
	
	public DlvSumHitching() {
		super(DlvDebet.class, "DlvDebet");
	}
	
	@Override
	public void onStart() {
		String sql = "update " + (new Delivery()).getTableName() + " set sumD = ? where id = ? and number = ?";
		try {
			stmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
//		super.onStart();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if(stmt != null) {
			DlvDebet dd = (DlvDebet)rawObject.createDataObject(dataObject);
			
			stmt.clearBindings();
			
			stmt.bindLong(1, dd.sum);
			stmt.bindString(2, dd.id);
			stmt.bindString(3, dd.number);
			
			stmt.execute();
		}
//		super.onRead(rawObject);
	}
	
	@Override
	public void onEnd() {
		if(stmt != null)
			stmt.close();
//		super.onEnd();
	}
}
