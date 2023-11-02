package com.grsoft.napoleon;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgStopEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;


class OrgStopHitching extends Hitching {
	SQLiteStatement stmt0, stmt1;	
	int count = 0;

	public OrgStopHitching() {
		super(OrgStopEx.class, "OrgStop");
	}
	
	@Override
	public void onStart() {
		String tableName = DataObjectInfo.getInstance().getTableName(Org.class);
		DbWriter.checkDBTable(DbObject.getDataType(Org.class));
		
		String sql = "UPDATE '" + tableName + "' SET debt=0, flags=(flags & " + Integer.toString(~Org.FL_STOP_LIST) + ") WHERE id=?";
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
		stmt0 = dataBase.compileStatement(sql);
		
		sql = "UPDATE '" + tableName + "' SET debt=?, flags=(flags | " + Integer.toString(Org.FL_STOP_LIST) + ") WHERE id=?";
		stmt1 = dataBase.compileStatement(sql);
	
		dataBase.beginTransaction();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws com.grsoft.network.exception.RuntimeException {
		if( stmt0 != null ) {
			OrgStopEx os = (OrgStopEx)rawObject.createDataObject(dataObject);
			if( os.debt > 0 ) {
				stmt1.clearBindings();
				stmt1.bindLong(1, os.debt);
				stmt1.bindString(2, os.id);			
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

