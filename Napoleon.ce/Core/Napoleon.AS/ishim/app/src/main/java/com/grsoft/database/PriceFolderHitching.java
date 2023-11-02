package com.grsoft.database;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceFolderOrder;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.sqlite.SQLiteStatement;

public class PriceFolderHitching extends Hitching {
	
	SQLiteStatement stmt = null;
	
	public PriceFolderHitching() {
		super(PriceFolderOrder.class, "PriceFolderOrder");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Price p = new Price();
		String sql = "update " + p.getTableName() + " set folderID = ?, [ord] = ? where id = ?";
		try {
			stmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		PriceFolderOrder dobj = (PriceFolderOrder) rawObject.createDataObject(dataObject);
		stmt.clearBindings();
		stmt.bindLong(1, dobj.folderID);
		stmt.bindLong(2, dobj.ord);
		stmt.bindString(3, dobj.id);
		
		try {
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if(stmt != null)
			stmt.close();
	}
}
