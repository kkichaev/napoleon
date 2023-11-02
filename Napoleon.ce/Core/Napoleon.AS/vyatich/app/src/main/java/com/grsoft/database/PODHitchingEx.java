package com.grsoft.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PODHitchingEx extends PODHitching {
	
	SQLiteStatement stmt, stmt2;

	SQLiteStatement createStmt(String table) {
		String sql = "UPDATE '" + table + "' set number=? where created=?";
		return DataBaseManager.getDataBase().compileStatement(sql);
	}

	@Override
	public void onStart() {
		super.onStart();

		try {
			stmt = createStmt(DataObjectInfo.getInstance().getTableName(Order.class));
			stmt2 = createStmt(DataObjectInfo.getInstance().getTableName(Return.class));
		} catch (Exception e) {

		}
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderProceededEx dobj = (OrderProceededEx) rawObject.createDataObject(dataObject);
		int flags = (dobj.remark.contains("заказобработан")) ? ParamState.ofProceeded : ParamState.ofExported; 
		handler.handle(dobj, flags);
		if( dobj.number != null && dobj.number.length() > 0 ) {
			try {
				SQLiteStatement runStmt = dobj.type.equals(ReturnDoc.instance().getObjectName()) ? stmt2 : stmt;
				if(runStmt != null) {
					runStmt.clearBindings();
					runStmt.bindString(1, dobj.number);
					runStmt.bindLong(2, dobj.created.getTime());

					runStmt.execute();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		if( stmt != null ) {
			stmt.close();
			stmt = null;
		}
		if( stmt2 != null ) {
			stmt2.close();
			stmt2 = null;
		}
	}
}
