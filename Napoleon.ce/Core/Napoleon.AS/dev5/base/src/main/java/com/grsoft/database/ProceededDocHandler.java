package com.grsoft.database;
import com.grsoft.aceteam.R;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Debug;

import android.database.sqlite.SQLiteStatement;

public class ProceededDocHandler {
	protected Map<DocTypeBase, SQLiteStatement> proceedStatusStmt = new HashMap<DocTypeBase, SQLiteStatement>();

	public ProceededDocHandler() {}
	
	protected String getParamStmt(DocType docType) {
		return "' SET params = (params | ?), podRemark = ? WHERE created = ?";
	}	
	
	public void handle(OrderProceeded proceeded, int param){
		DocType dt = (DocType) (proceeded.type.length() == 0 
				? OrderDoc.instance()
				: DocType.getDocType(proceeded.type));

		if(dt != null){
			String table = DataObjectInfo.getInstance().getTableName(dt.dataType());
			if( table != null && table.length() > 0 ) {
				SQLiteStatement stmt = null;
				
				if (!proceedStatusStmt.containsKey(dt.getClass())){
					DbWriter.checkDBTable(dt.dataType());
					StringBuilder sql = new StringBuilder();
					sql.append("UPDATE '").
					append(table).
					append(getParamStmt(dt));
					stmt = DataBaseManager.getDataBase().compileStatement(sql.toString());
					
					proceedStatusStmt.put(dt, stmt);
				}else
					stmt = proceedStatusStmt.get(dt);
				 		
				stmt.clearBindings();
		        bindArgs(proceeded, param, stmt);
				
				StringBuilder sb = new StringBuilder();
				sb.append("PODHitching exec created: ").append(proceeded.created.getTime()).append('\n');
				Debug.putLog(sb.toString());
				
				stmt.execute();
			}
		}
	}

	protected void bindArgs(OrderProceeded proceeded, int param, SQLiteStatement stmt) {
		stmt.bindLong(1, param);
		stmt.bindString(2, proceeded.remark);
		stmt.bindLong(3, proceeded.created.getTime());
	}	
	
	public void clear(){
		for(SQLiteStatement stmt: proceedStatusStmt.values())
			stmt.close();		
		proceedStatusStmt.clear();
	}
}
