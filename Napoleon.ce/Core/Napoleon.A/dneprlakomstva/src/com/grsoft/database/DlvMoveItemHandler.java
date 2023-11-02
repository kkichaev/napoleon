package com.grsoft.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DlvMoveItem;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.OrderEx;

public class DlvMoveItemHandler {
	protected SQLiteStatement incassStmt;
	protected SQLiteStatement ordStmt;

	public DlvMoveItemHandler() {
		
	}
	
	public void onStart() {
		try{
			String table = DataObjectInfo.getInstance().getTableName(IncassDebDistrEx.class);
			SQLiteDatabase database = DataBaseManager.getDataBase();
			incassStmt = database.compileStatement("UPDATE " + table + " set docNumber=? where created=?");
			
			table = DataObjectInfo.getInstance().getTableName(OrderEx.class);
			ordStmt = database.compileStatement("UPDATE " + table + " set incassNum=? where created=?");
		}catch(Exception e){}
	}
	
	public void onEnd() {
		if( incassStmt != null )
			incassStmt.close();
		if( ordStmt != null )
			ordStmt.close();
	}
	
	public void onStep(DlvMoveItem item) {
		SQLiteStatement stmt = null;
		
		if(incassStmt != null && item.type.equals("Incass")) {
			stmt = incassStmt;
		} else if(ordStmt != null && item.type.equals(OrderEx.ORD_INCASS)) {
			stmt = ordStmt;
//		} else if(retStmt != null && item.type.equals("Returns")) {
//			stmt = retStmt;
		}

		if( stmt != null ) {
			stmt.clearBindings();
			stmt.bindString(1, item.num);
			stmt.bindLong(2, item.created.getTime());
			try{
				stmt.execute();
			}catch(Exception e){}
		}
		
	}
}
