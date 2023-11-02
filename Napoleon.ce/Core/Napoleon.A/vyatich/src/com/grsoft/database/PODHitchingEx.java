package com.grsoft.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PODHitchingEx extends PODHitching {
	
	SQLiteStatement stmt;
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderProceededEx dobj = (OrderProceededEx) rawObject.createDataObject(dataObject);
		int flags = (dobj.remark.contains("заказобработан")) ? ParamState.ofProceeded : ParamState.ofExported; 
		handler.handle(dobj, flags);
		if( dobj.number != null && dobj.number.length() > 0 ) {
			try {
				if( stmt == null ) {
					String table = DataObjectInfo.getInstance().getTableName(Order.class);
					String sql = "UPDATE '" + table + "' set number=? where created=?";
					stmt = DataBaseManager.getDataBase().compileStatement(sql);
				}
				
				stmt.clearBindings();
				stmt.bindString(1, dobj.number);
				stmt.bindLong(2, dobj.created.getTime());
				
				stmt.execute();
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
	}
}
