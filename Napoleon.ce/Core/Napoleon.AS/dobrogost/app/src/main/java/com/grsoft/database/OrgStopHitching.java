package com.grsoft.database;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgStopEx;
import com.grsoft.network.RawObject;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class OrgStopHitching extends Hitching {
	SQLiteStatement stmt0, stmt1;	
	int count = 0;

	public OrgStopHitching() {
		super(OrgStopEx.class, "OrgStop");
	}
	
	@Override
	public void onStart() {
		String tableName = DataObjectInfo.getInstance().getTableName(Org.class);
		if( DbWriter.isTableExists(tableName) ) {
			String sql = "UPDATE '" + tableName + "' SET flags=(flags & " + Integer.toString(~Org.FL_STOP_LIST) + ") WHERE id=?";
			SQLiteDatabase dataBase = DataBaseManager.getDataBase();
			stmt0 = dataBase.compileStatement(sql);
			
			sql = "UPDATE '" + tableName + "' SET flags=(flags | " + Integer.toString(Org.FL_STOP_LIST) + ") WHERE id=?";
			stmt1 = dataBase.compileStatement(sql);
		
			dataBase.beginTransaction();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws com.grsoft.network.exception.RuntimeException {
		if( stmt0 != null ) {
			OrgStopEx os = (OrgStopEx)rawObject.createDataObject(dataObject);
			if( os.stop > 0 ) {
				stmt1.clearBindings();
				stmt1.bindString(1, os.id);			
				stmt1.execute();			
			} else {
				stmt0.clearBindings();
				stmt0.bindString(1, os.id);			
				stmt0.execute();			
			}
			
			if( count++ >= 500 ) {
				count = 0;
				SQLiteDatabase dataBase = DataBaseManager.getDataBase();
				dataBase.setTransactionSuccessful();
				dataBase.endTransaction();
				dataBase.beginTransaction();
			}
		}
	}
	
	@Override
	public void onEnd() {
		if( stmt0 != null ) {
			stmt0.close();
			stmt0 = null;
		}
		if( stmt1 != null ) {
			stmt1.close();
			stmt1 = null;
		}

		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		dataBase.setTransactionSuccessful();
		dataBase.endTransaction();
	}	
}
