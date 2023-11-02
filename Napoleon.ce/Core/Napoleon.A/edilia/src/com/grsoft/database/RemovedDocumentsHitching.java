package com.grsoft.database;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.RemovedDocuments;

import android.database.sqlite.SQLiteStatement;

public class RemovedDocumentsHitching extends Hitching {
	SQLiteStatement incassStmt, incassStmt2;
	SQLiteStatement orderStmt;
	SQLiteStatement dlvStmt;
	
	public RemovedDocumentsHitching() {
		super(RemovedDocuments.class, "RemovedDocuments");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		String sql;
		
		String tn = (new Incass()).getTableName();
		try {
			sql = "update [" + tn + "]  set docNumber = '' where docNumber = ?";
			incassStmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tn = (new Order()).getTableName();
		try {
			sql = "update [" + tn + "] set incassNum = '' where incassNum = ?";
			incassStmt2 = DataBaseManager.getDataBase().compileStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			sql = "update [" + tn + "] set ordNumber = '' where ordNumber = ?";
			orderStmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tn = (new Delivery()).getTableName();
		try {
			sql = "delete from [" + tn + "] where number = ?";
			dlvStmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onRead(com.grsoft.network.RawObject rawObject) throws com.grsoft.network.exception.RuntimeException {
		RemovedDocuments rd = (RemovedDocuments) rawObject.createDataObject(dataObject);
		if(rd.type.equals("Incass")) {
			if(incassStmt != null) {
				incassStmt.clearBindings();
				incassStmt.bindString(1, rd.number);
				incassStmt.execute();
			}

			if(incassStmt2 != null) {
				incassStmt2.clearBindings();
				incassStmt2.bindString(1, rd.number);
				incassStmt2.execute();
			}
		} else if(rd.type.equals("Delivery")) {
			if(orderStmt != null) {
				orderStmt.clearBindings();
				orderStmt.bindString(1, rd.number);
				orderStmt.execute();
			}

			if(dlvStmt != null) {
				dlvStmt.clearBindings();
				dlvStmt.bindString(1, rd.number);
				dlvStmt.execute();
			}
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		if(incassStmt != null) {
			incassStmt.close();
			incassStmt = null;
		}
		if(incassStmt2 != null) {
			incassStmt2.close();
			incassStmt2 = null;
		}
		if(orderStmt != null) {
			orderStmt.close();
			orderStmt = null;
		}
		if(dlvStmt != null) {
			dlvStmt.close();
			dlvStmt = null;
		}
	}
}
