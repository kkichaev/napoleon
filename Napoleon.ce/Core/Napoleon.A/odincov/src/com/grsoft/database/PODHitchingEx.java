package com.grsoft.database;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Debug;

public class PODHitchingEx extends PODHitching {
	
	@Override
	protected ProceededDocHandler createHandler() {
		return new Handler();
	}
}

class Handler extends ProceededDocHandler {
	@Override
	public void handle(OrderProceeded proceeded, int param) {
		DocType dt = (DocType) (proceeded.type.length() == 0 
				? OrderDoc.instance()
				: DocType.getDocType(proceeded.type));
		if( dt == OrderDoc.instance() ) {
			SQLiteStatement stmt = null;
			
			String table = DataObjectInfo.getInstance().getTableName(dt.dataType());
			if (!proceedStatusStmt.containsKey(dt.getClass())){
				DbWriter.checkDBTable(dt.dataType());
				StringBuilder sql = new StringBuilder();
				sql.append("UPDATE '").
				append(table).
				append("' SET params = (params | ?), podRemark = ?, phone = ?, forwarder = ?  WHERE created = ?" );
				stmt = DataBaseManager.getDataBase().compileStatement(sql.toString());
				
				proceedStatusStmt.put(dt, stmt);
			}else
				stmt = proceedStatusStmt.get(dt);
			
			stmt.clearBindings();
			OrderProceededEx ope = (OrderProceededEx)proceeded;
			int pos = 1;
	        stmt.bindLong(pos++, param);
			stmt.bindString(pos++, proceeded.remark);
			stmt.bindString(pos++, ope.phone);
			stmt.bindString(pos++, ope.forwarder);
			stmt.bindLong(pos++, proceeded.created.getTime());
			
			StringBuilder sb = new StringBuilder();
			sb.append("PODHitching exec created: ").append(proceeded.created.getTime()).append('\n');
			Debug.putLog(sb.toString());
			
			stmt.execute();

			return;
		}
		
		super.handle(proceeded, param);
	}
}
