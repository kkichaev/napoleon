package com.grsoft.database;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnRestore;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.sqlite.SQLiteStatement;

public class RestoreReturnNumbers extends DataObjectRestore {
	SQLiteStatement stmt;
	
	public RestoreReturnNumbers() {
		super(ReturnRestore.class, "ArchiveReturnNumbers", "created");
		
		try {
			String sql = "update [" + DataObjectInfo.getInstance().getTableName(Return.class) + "] set retNumber=? where created=?";
			stmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ReturnRestore rr = (ReturnRestore) rawObject.createDataObject(dataObject);
		if(stmt != null) {
			stmt.clearBindings();
			stmt.bindString(1, rr.number);
			stmt.bindLong(2, rr.created.getTime());
			
			stmt.execute();
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if( stmt != null)
			stmt.close();
	}
}
