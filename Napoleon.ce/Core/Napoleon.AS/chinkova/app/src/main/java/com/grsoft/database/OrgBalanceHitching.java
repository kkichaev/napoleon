package com.grsoft.database;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class OrgBalanceHitching extends Hitching {
	SQLiteStatement stmt;

	public OrgBalanceHitching() {
		super(OrgEx.class, "Org");
	}
	
	@Override
	public void onStart() {
		super.onStart();

		OrgEx o = new OrgEx();
		DbWriter.checkDBTable(Org.class);
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		String query = "update " + o.getTableName() + " SET debt = ? where id = ?";
		stmt = db.compileStatement(query);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		if(stmt != null)
			stmt.close();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrgEx o = (OrgEx)rawObject.createDataObject(dataObject);
		stmt.clearBindings();
		stmt.bindLong(1, o.debt);
		stmt.bindString(2, o.id);
		stmt.execute();
	}
}
