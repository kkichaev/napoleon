package com.grsoft.database;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SchFactNumber;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.sqlite.SQLiteStatement;

public class SchFactHitching extends Hitching {
	SQLiteStatement stmt = null;
	
	public SchFactHitching() {
		super(SchFactNumber.class, "SchFactNumber");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Sales s = new Sales();
		DbWriter.checkDBTable(s.getClass());
		
		stmt = DataBaseManager.getDataBase().compileStatement("UPDATE " + s.getTableName() + " set schFactNumber=? where created=?");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		SchFactNumber obj = (SchFactNumber) rawObject.createDataObject(dataObject);
		
		try {
			stmt.clearBindings();
			stmt.bindLong(2, obj.created.getTime());
			stmt.bindString(1, obj.number);
			stmt.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if(stmt != null)
			stmt.close();
		stmt = null;
	}
}
