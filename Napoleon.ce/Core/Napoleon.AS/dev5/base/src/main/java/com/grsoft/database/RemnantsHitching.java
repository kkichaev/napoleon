package com.grsoft.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceRemnants;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class RemnantsHitching extends Hitching {
	SQLiteStatement stmt;
	int count = 0;
	
	public RemnantsHitching() { super(PriceRemnants.class, "PriceRemnants"); }

	@Override
	public void onStart() {
		String tableName = DataObjectInfo.getInstance().getTableName(Price.class);
		if( DbWriter.isTableExists(tableName) ) {
			String sql = "UPDATE '" + tableName + "' SET qty=0";
			SQLiteDatabase dataBase = DataBaseManager.getDataBase();
			dataBase.execSQL(sql);
			
			sql = "UPDATE '" + tableName + "' SET qty=? WHERE id=?";
			stmt = DataBaseManager.getDataBase().compileStatement(sql);
			dataBase.beginTransaction();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if( stmt != null ) {
			PriceRemnants dobj = (PriceRemnants) rawObject.createDataObject(dataObject);
			
			stmt.clearBindings();
			stmt.bindLong(1, dobj.qty);
			stmt.bindString(2, dobj.id);
			
			stmt.execute();
			
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
		if( stmt != null )
			stmt.close();
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		dataBase.setTransactionSuccessful();
		dataBase.endTransaction();
	}
}
